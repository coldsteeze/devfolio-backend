package korobkin.nikita.auth_service.integration;

import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.exception.EmailAlreadyExistsException;
import korobkin.nikita.auth_service.exception.InvalidCredentialsException;
import korobkin.nikita.auth_service.exception.InvalidRefreshTokenException;
import korobkin.nikita.auth_service.fixtures.AuthRequestFixtures;
import korobkin.nikita.auth_service.fixtures.JwtTokenFixtures;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.events.UserDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }

    @Test
    void registerUser_success() {
        JwtTokens tokens = authService.register(AuthRequestFixtures.registerRequest());

        User user = userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL).orElseThrow();

        assertThat(user.getEmail()).isEqualTo(AuthRequestFixtures.VALID_EMAIL);
        assertThat(tokens.getAccessToken()).isNotEmpty();
        assertThat(tokens.getRefreshToken()).isNotEmpty();
    }

    @Test
    void registerUser_duplicateEmail_fails() {
        authService.register(AuthRequestFixtures.registerRequest());

        assertThatThrownBy(() -> authService.register(AuthRequestFixtures.registerRequest()))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    void registerUser_passwordIsEncoded() {
        authService.register(AuthRequestFixtures.registerRequest());

        User user = userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL).orElseThrow();

        assertThat(passwordEncoder.matches(
                AuthRequestFixtures.VALID_PASSWORD,
                user.getPassword()
        )).isTrue();
    }

    @Test
    void registerUser_refreshTokenStoredInRedis() {
        JwtTokens tokens = authService.register(AuthRequestFixtures.registerRequest());

        User user = userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL).orElseThrow();

        String stored = redisTemplate.opsForValue().get("refresh:" + user.getId());
        assertThat(stored).isEqualTo(tokens.getRefreshToken());
    }

    @Test
    void loginUser_success() {
        authService.register(AuthRequestFixtures.registerRequest());

        JwtTokens tokens = authService.login(AuthRequestFixtures.loginRequest());

        assertThat(tokens.getAccessToken()).isNotEmpty();
        assertThat(tokens.getRefreshToken()).isNotEmpty();
    }

    @Test
    void login_success_overwritesRefreshToken() {
        authService.register(AuthRequestFixtures.registerRequest());

        JwtTokens firstLogin = authService.login(AuthRequestFixtures.loginRequest());

        JwtTokens secondLogin = authService.login(AuthRequestFixtures.loginRequest());

        User user = userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL).orElseThrow();
        String stored = redisTemplate.opsForValue().get("refresh:" + user.getId());

        assertThat(firstLogin.getRefreshToken()).isNotEqualTo(secondLogin.getRefreshToken());
        assertThat(stored).isEqualTo(secondLogin.getRefreshToken());
    }

    @Test
    void loginUser_invalidCredentials_fails() {
        authService.register(AuthRequestFixtures.registerRequest());

        assertThatThrownBy(() -> authService.login(AuthRequestFixtures.loginRequestWithInvalidCredentials()))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void refreshToken_success_rotatesTokens() {
        authService.register(AuthRequestFixtures.registerRequest());

        JwtTokens loginTokens = authService.login(AuthRequestFixtures.loginRequest());

        JwtTokens refreshed = authService.refreshToken(loginTokens.getRefreshToken());

        User user = userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL).orElseThrow();

        String stored = redisTemplate.opsForValue().get("refresh:" + user.getId());

        assertThat(loginTokens.getRefreshToken()).isNotEqualTo(refreshed.getRefreshToken());
        assertThat(refreshed.getRefreshToken()).isEqualTo(stored);
    }

    @Test
    void refreshToken_oldTokenFails() {
        authService.register(AuthRequestFixtures.registerRequest());

        JwtTokens oldTokens = authService.login(AuthRequestFixtures.loginRequest());

        authService.login(AuthRequestFixtures.loginRequest());

        assertThatThrownBy(() -> authService.refreshToken(oldTokens.getRefreshToken()))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("Unauthorized");
    }


    @Test
    void refreshToken_invalidToken_fails() {
        assertThatThrownBy(() -> authService.refreshToken(JwtTokenFixtures.INVALID_TOKEN))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void refreshToken_deletedUser_fails() {
        authService.register(AuthRequestFixtures.registerRequest());

        JwtTokens tokens = authService.login(AuthRequestFixtures.loginRequest());

        User user = userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL).orElseThrow();

        userRepository.delete(user);

        assertThatThrownBy(() -> authService.refreshToken(tokens.getRefreshToken()))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void logout_removesRefreshToken() {
        authService.register(AuthRequestFixtures.registerRequest());

        JwtTokens removedTokens = authService.login(AuthRequestFixtures.loginRequest());

        authService.logout(removedTokens.getRefreshToken());

        User user = userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL).orElseThrow();

        String stored = redisTemplate.opsForValue().get("refresh:" + user.getId());

        assertThat(stored).isNullOrEmpty();
    }

    @Test
    void deleteUser_success() {
        authService.register(AuthRequestFixtures.registerRequest());

        User user = userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL)
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        assertThat(userRepository.findById(user.getId())).isPresent();

        authService.deleteUser(new UserDeletedEvent(UUID.randomUUID(), user.getId()));

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}

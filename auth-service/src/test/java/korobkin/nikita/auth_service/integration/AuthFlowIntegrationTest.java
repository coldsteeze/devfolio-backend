package korobkin.nikita.auth_service.integration;

import com.auth0.jwt.exceptions.JWTVerificationException;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RefreshTokenRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.dto.response.JwtResponse;
import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.exception.InvalidCredentialsException;
import korobkin.nikita.auth_service.exception.UserAlreadyExistsException;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.events.UserDeletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@mail.com");
        request.setPassword("pass123");

        JwtResponse tokens = authService.register(request);
        User user = userRepository.findByEmail("newuser@mail.com").orElseThrow();

        assertThat(user.getEmail()).isEqualTo("newuser@mail.com");
        assertThat(tokens.getAccessToken()).isNotEmpty();
        assertThat(tokens.getRefreshToken()).isNotEmpty();
    }

    @Test
    void registerUser_duplicateEmail_fails() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("dup@mail.com");
        request.setPassword("pass123");
        authService.register(request);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void loginUser_success() {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("login@mail.com");
        register.setPassword("secret");
        authService.register(register);

        LoginRequest login = new LoginRequest();
        login.setEmail("login@mail.com");
        login.setPassword("secret");

        JwtResponse tokens = authService.login(login);
        assertThat(tokens.getAccessToken()).isNotEmpty();
        assertThat(tokens.getRefreshToken()).isNotEmpty();
    }

    @Test
    void loginUser_invalidPassword_fails() {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("wrongpass@mail.com");
        register.setPassword("correct");
        authService.register(register);

        LoginRequest login = new LoginRequest();
        login.setEmail("wrongpass@mail.com");
        login.setPassword("incorrect");

        assertThatThrownBy(() -> authService.login(login))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void refreshToken_storedInRedis() {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("refresh@mail.com");
        register.setPassword("pass123");
        authService.register(register);

        LoginRequest login = new LoginRequest();
        login.setEmail("refresh@mail.com");
        login.setPassword("pass123");
        JwtResponse tokens = authService.login(login);

        User user = userRepository.findByEmail("refresh@mail.com").orElseThrow();

        String storedRefresh = redisTemplate.opsForValue().get("refresh:" + user.getId());
        assertThat(storedRefresh).isEqualTo(tokens.getRefreshToken());
    }

    @Test
    void refreshToken_invalidToken_fails() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-token");

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void deleteUser_success() {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("deleted@mail.ru");
        register.setPassword("pass123");
        authService.register(register);

        User user = userRepository.findByEmail("deleted@mail.ru")
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        assertThat(userRepository.findById(user.getId())).isPresent();

        authService.deleteUser(new UserDeletedEvent(user.getId()));

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}

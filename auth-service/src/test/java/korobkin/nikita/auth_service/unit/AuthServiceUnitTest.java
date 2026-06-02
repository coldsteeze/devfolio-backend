package korobkin.nikita.auth_service.unit;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.entity.enums.UserRole;
import korobkin.nikita.auth_service.exception.EmailAlreadyExistsException;
import korobkin.nikita.auth_service.exception.InvalidCredentialsException;
import korobkin.nikita.auth_service.exception.InvalidRefreshTokenException;
import korobkin.nikita.auth_service.fixtures.AuthRequestFixtures;
import korobkin.nikita.auth_service.fixtures.JwtTokenFixtures;
import korobkin.nikita.auth_service.fixtures.UserFixtures;
import korobkin.nikita.auth_service.mapper.UserMapper;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.security.jwt.JwtProperties;
import korobkin.nikita.auth_service.security.jwt.JwtService;
import korobkin.nikita.auth_service.security.user.UserDetailsImpl;
import korobkin.nikita.auth_service.service.OutboxEventService;
import korobkin.nikita.auth_service.service.TokenCacheService;
import korobkin.nikita.auth_service.service.impl.AuthServiceImpl;
import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.events.UserDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService unit tests")
public class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenCacheService tokenService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private OutboxEventService outboxEventService;

    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User user;
    private User savedUser;
    private UUID userId;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = AuthRequestFixtures.registerRequest();

        user = UserFixtures.defaultUser();

        savedUser = UserFixtures.savedUser();
        userId = savedUser.getId();

        loginRequest = AuthRequestFixtures.loginRequest();

        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(savedUser);
        authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, loginRequest.getPassword());
    }

    @Nested
    @DisplayName("User registration")
    class Register {

        @Test
        @DisplayName("Should return JWT tokens when user is successfully registered")
        void register_shouldReturnJwtTokens_whenNewUser() {
            mockSuccessfulRegistration();
            doNothing().when(tokenService).saveRefreshToken(any(UUID.class), anyString(), anyLong());

            JwtTokens tokens = authService.register(registerRequest);

            verify(outboxEventService).saveEvent(
                    eq("USER"),
                    eq(userId),
                    eq("user-created"),
                    any(UserCreatedEvent.class)
            );

            assertThat(user.getLoggedAt()).isNotNull();
            assertThat(tokens.getAccessToken()).isEqualTo(JwtTokenFixtures.DEFAULT_ACCESS_TOKEN);
            assertThat(tokens.getRefreshToken()).isEqualTo(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            assertThat(tokens.getAccessTokenExpiresIn()).isEqualTo(JwtTokenFixtures.DEFAULT_EXPIRES_IN);
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void register_shouldThrowException_whenEmailAlreadyExists() {
            given(userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL)).willReturn(Optional.of(user));

            assertThatThrownBy(() -> authService.register(registerRequest))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining("Email already exists");
        }

        @Test
        @DisplayName("Should encode password before saving user")
        void register_shouldEncodePassword_beforeSavingUser() {
            given(userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL)).willReturn(Optional.empty());
            given(userMapper.toEntity(registerRequest)).willReturn(user);
            given(passwordEncoder.encode(AuthRequestFixtures.VALID_PASSWORD))
                    .willReturn(UserFixtures.ENCODED_PASSWORD);

            authService.register(registerRequest);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());

            assertThat(captor.getValue().getPassword())
                    .isEqualTo(UserFixtures.ENCODED_PASSWORD);
        }

        @Test
        @DisplayName("Should save refresh token in token service")
        void register_shouldSaveRefreshToken_inTokenService() {
            given(userRepository.findByEmail(AuthRequestFixtures.VALID_EMAIL)).willReturn(Optional.empty());
            given(userMapper.toEntity(registerRequest)).willReturn(user);
            given(passwordEncoder.encode(AuthRequestFixtures.VALID_PASSWORD))
                    .willReturn(UserFixtures.ENCODED_PASSWORD);
            given(userRepository.save(any(User.class))).willAnswer(invocation -> {
                User u = invocation.getArgument(0);
                u.setId(userId);
                return u;
            });
            given(jwtService.generateAccessToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL), eq(UserRole.ROLE_USER.name())))
                    .willReturn(JwtTokenFixtures.DEFAULT_ACCESS_TOKEN);
            given(jwtService.generateRefreshToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL)))
                    .willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

            authService.register(registerRequest);

            assertThat(user.getLoggedAt()).isNotNull();
            verify(tokenService).saveRefreshToken(userId, JwtTokenFixtures.DEFAULT_REFRESH_TOKEN, 7L);
        }
    }

    @Nested
    @DisplayName("User authentication")
    class Login {

        @Test
        @DisplayName("Should return JWT tokens when credentials are valid")
        void login_shouldReturnJwtTokens_whenCredentialsAreValid() {
            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(authentication);
            given(jwtService.generateAccessToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL), eq(UserRole.ROLE_USER.name())))
                    .willReturn(JwtTokenFixtures.DEFAULT_ACCESS_TOKEN);
            given(jwtService.generateRefreshToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL)))
                    .willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            given(jwtProperties.getAccessTokenExpirationMinutes()).willReturn(JwtTokenFixtures.DEFAULT_EXPIRES_IN);
            given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

            JwtTokens tokens = authService.login(loginRequest);

            assertThat(savedUser.getLoggedAt()).isNotNull();
            assertThat(tokens.getAccessToken()).isEqualTo(JwtTokenFixtures.DEFAULT_ACCESS_TOKEN);
            assertThat(tokens.getRefreshToken()).isEqualTo(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            assertThat(tokens.getAccessTokenExpiresIn()).isEqualTo(JwtTokenFixtures.DEFAULT_EXPIRES_IN);
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
        void login_shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {
            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willThrow(new BadCredentialsException("e"));

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessageContaining("Invalid email or password");
        }

        @Test
        @DisplayName("Should save refresh token when login is successful")
        void login_shouldSaveRefreshToken_whenLoginSuccess() {
            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(authentication);

            given(jwtService.generateAccessToken(eq(savedUser.getId()), eq(AuthRequestFixtures.VALID_EMAIL), eq(UserRole.ROLE_USER.name())))
                    .willReturn(JwtTokenFixtures.DEFAULT_ACCESS_TOKEN);
            given(jwtService.generateRefreshToken(eq(savedUser.getId()), eq(AuthRequestFixtures.VALID_EMAIL)))
                    .willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

            authService.login(loginRequest);

            verify(tokenService).saveRefreshToken(eq(savedUser.getId()), eq(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN), eq(7L));
            assertThat(savedUser.getLoggedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Refresh token processing")
    class RefreshToken {

        @Test
        @DisplayName("Should return new JWT tokens when refresh token is valid")
        void refreshToken_shouldReturnNewJwtTokens_whenTokenIsValid() {
            DecodedJWT jwt = mock(DecodedJWT.class);
            given(jwtService.verify(any(String.class))).willReturn(jwt);
            given(jwtService.isRefreshToken(jwt)).willReturn(true);
            given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
            given(jwtService.getEmailFromVerifiedToken(jwt)).willReturn(AuthRequestFixtures.VALID_EMAIL);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(tokenService.getRefreshToken(userId)).willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            given(jwtService.generateAccessToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL), eq(UserRole.ROLE_USER.name())))
                    .willReturn(JwtTokenFixtures.NEW_ACCESS_TOKEN);
            given(jwtService.generateRefreshToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL)))
                    .willReturn(JwtTokenFixtures.NEW_REFRESH_TOKEN);
            given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

            JwtTokens tokens = authService.refreshToken(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            verify(tokenService).saveRefreshToken(userId, JwtTokenFixtures.NEW_REFRESH_TOKEN, 7L);
            assertThat(tokens.getAccessToken()).isEqualTo(JwtTokenFixtures.NEW_ACCESS_TOKEN);
            assertThat(tokens.getRefreshToken()).isEqualTo(JwtTokenFixtures.NEW_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("Should throw exception when refresh token is missing in cache")
        void refreshToken_shouldThrowInvalidRefreshTokenException_whenTokenIsInvalid() {
            DecodedJWT jwt = mock(DecodedJWT.class);
            given(jwtService.verify(any(String.class))).willReturn(jwt);
            given(jwtService.isRefreshToken(jwt)).willReturn(true);
            given(jwtService.getUserIdFromVerifiedToken(any(DecodedJWT.class))).willReturn(userId);
            given(jwtService.getEmailFromVerifiedToken(any(DecodedJWT.class)))
                    .willReturn(AuthRequestFixtures.VALID_EMAIL);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(tokenService.getRefreshToken(userId)).willReturn(null);

            assertThatThrownBy(() -> authService.refreshToken(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN))
                    .isInstanceOf(InvalidRefreshTokenException.class)
                    .hasMessageContaining("Unauthorized");
        }

        @Test
        @DisplayName("Should throw exception when refresh token does not match stored token")
        void refreshToken_shouldThrowInvalidRefreshTokenException_whenTokenDoesNotMatchStored() {
            DecodedJWT jwt = mock(DecodedJWT.class);
            given(jwtService.verify(any(String.class))).willReturn(jwt);
            given(jwtService.isRefreshToken(jwt)).willReturn(true);
            given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
            given(jwtService.getEmailFromVerifiedToken(jwt)).willReturn(AuthRequestFixtures.VALID_EMAIL);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(tokenService.getRefreshToken(userId)).willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);

            assertThatThrownBy(() -> authService.refreshToken(JwtTokenFixtures.INVALID_TOKEN))
                    .isInstanceOf(InvalidRefreshTokenException.class)
                    .hasMessageContaining("Unauthorized");
        }

        @Test
        @DisplayName("Should save new refresh token after successful refresh")
        void refreshToken_shouldSaveNewRefreshToken_inTokenService() {
            DecodedJWT jwt = mock(DecodedJWT.class);
            given(jwtService.verify(any(String.class))).willReturn(jwt);
            given(jwtService.isRefreshToken(jwt)).willReturn(true);
            given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
            given(jwtService.getEmailFromVerifiedToken(jwt)).willReturn(AuthRequestFixtures.VALID_EMAIL);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(tokenService.getRefreshToken(userId)).willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            given(jwtService.generateAccessToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL), eq(UserRole.ROLE_USER.name())))
                    .willReturn(JwtTokenFixtures.NEW_ACCESS_TOKEN);
            given(jwtService.generateRefreshToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL)))
                    .willReturn(JwtTokenFixtures.NEW_REFRESH_TOKEN);
            given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

            authService.refreshToken(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);

            verify(tokenService).saveRefreshToken(userId, JwtTokenFixtures.NEW_REFRESH_TOKEN, 7L);
        }

        @Test
        @DisplayName("Should throw exception when user does not exist")
        void refreshToken_shouldThrowInvalidRefreshToken_whenUserDoesNotExist() {
            DecodedJWT jwt = mock(DecodedJWT.class);
            given(jwtService.verify(any(String.class))).willReturn(jwt);
            given(jwtService.isRefreshToken(jwt)).willReturn(true);
            given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
            given(jwtService.getEmailFromVerifiedToken(jwt)).willReturn(AuthRequestFixtures.VALID_EMAIL);
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.refreshToken(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN))
                    .isInstanceOf(InvalidRefreshTokenException.class)
                    .hasMessageContaining("Unauthorized");
        }

        @Test
        @DisplayName("Should throw exception when JWT verification fails")
        void refreshToken_shouldThrowException_whenJwtServiceFails() {
            given(jwtService.verify(any(String.class)))
                    .willThrow(new JWTVerificationException("Unauthorized"));

            assertThatThrownBy(() -> authService.refreshToken(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN))
                    .isInstanceOf(InvalidRefreshTokenException.class)
                    .hasMessageContaining("Unauthorized");

            verify(tokenService, never()).saveRefreshToken(any(), any(), anyLong());
        }
    }

    @Nested
    @DisplayName("User logout")
    class Logout {

        @Test
        @DisplayName("Should delete refresh token from token service")
        void logout_shouldDeleteRefreshToken_fromTokenService() {
            DecodedJWT jwt = mock(DecodedJWT.class);
            given(jwtService.verify(anyString())).willReturn(jwt);
            given(jwtService.isRefreshToken(jwt)).willReturn(true);
            given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
            doNothing().when(tokenService).deleteRefreshToken(userId);

            authService.logout(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);

            verify(tokenService).deleteRefreshToken(userId);
        }

        @Test
        @DisplayName("Should do nothing when token is not refresh")
        void logout_shouldDoNothing_whenTokenIsNotRefreshToken() {
            DecodedJWT jwt = mock(DecodedJWT.class);
            given(jwtService.verify(anyString())).willReturn(jwt);
            given(jwtService.isRefreshToken(jwt)).willReturn(false);

            authService.logout(JwtTokenFixtures.INVALID_TOKEN);

            verify(jwtService).verify(JwtTokenFixtures.INVALID_TOKEN);
            verify(jwtService).isRefreshToken(jwt);
            verify(tokenService, never()).deleteRefreshToken(any(UUID.class));
        }
    }

    @Nested
    @DisplayName("User deletion event handling")
    class DeleteUser {

        @Test
        @DisplayName("Should delete user and refresh token when UserDeletedEvent is received")
        void deleteUser_shouldDeleteUser_andRefreshToken_whenUserExists() {
            authService.deleteUser(new UserDeletedEvent(UUID.randomUUID(), userId));

            verify(userRepository).deleteById(userId);
            verify(tokenService).deleteRefreshToken(userId);
        }
    }

    private void mockSuccessfulRegistration() {
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userMapper.toEntity(any(RegisterRequest.class))).willReturn(user);
        given(passwordEncoder.encode(anyString())).willReturn(UserFixtures.ENCODED_PASSWORD);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(userId);
            return u;
        });
        given(jwtService.generateAccessToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL), eq(UserRole.ROLE_USER.name())))
                .willReturn(JwtTokenFixtures.DEFAULT_ACCESS_TOKEN);
        given(jwtService.generateRefreshToken(any(UUID.class), eq(AuthRequestFixtures.VALID_EMAIL)))
                .willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
        given(jwtProperties.getAccessTokenExpirationMinutes()).willReturn(JwtTokenFixtures.DEFAULT_EXPIRES_IN);
        given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(1L);
    }
}

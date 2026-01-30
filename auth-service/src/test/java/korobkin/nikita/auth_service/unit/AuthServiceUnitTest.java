package korobkin.nikita.auth_service.unit;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import korobkin.nikita.auth_service.security.jwt.JwtProperties;
import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.exception.EmailAlreadyExistsException;
import korobkin.nikita.auth_service.exception.InvalidCredentialsException;
import korobkin.nikita.auth_service.exception.InvalidRefreshTokenException;
import korobkin.nikita.auth_service.kafka.producer.UserEventProducer;
import korobkin.nikita.auth_service.mapper.UserMapper;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.security.jwt.impl.JwtServiceImpl;
import korobkin.nikita.auth_service.security.user.UserDetailsImpl;
import korobkin.nikita.auth_service.service.TokenCacheService;
import korobkin.nikita.auth_service.service.impl.AuthServiceImpl;
import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.events.UserDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
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
public class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtServiceImpl jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenCacheService tokenService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private UserEventProducer userEventProducer;

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
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@mail.com");
        registerRequest.setPassword("password");

        user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("password");

        userId = UUID.randomUUID();
        savedUser = new User();
        savedUser.setId(userId);
        savedUser.setEmail("test@mail.com");
        savedUser.setPassword("encodedPassword");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@mail.com");
        loginRequest.setPassword("password");

        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(savedUser);
        authentication = new UsernamePasswordAuthenticationToken(userDetailsImpl, loginRequest.getPassword());
    }

    @Test
    void register_shouldReturnJwtTokens_whenNewUser() {
        mockSuccessfulRegistration();
        doNothing().when(tokenService).saveRefreshToken(any(UUID.class), anyString(), anyLong());

        JwtTokens tokens = authService.register(registerRequest);

        assertThat(user.getLoggedAt()).isNotNull();
        assertThat(tokens.getAccessToken()).isEqualTo("access");
        assertThat(tokens.getRefreshToken()).isEqualTo("refresh");
        assertThat(tokens.getAccessTokenExpiresIn()).isEqualTo(15L);
    }


    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        given(userRepository.findByEmail("test@mail.com")).willReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void register_shouldEncodePassword_beforeSavingUser() {
        given(userRepository.findByEmail("test@mail.com")).willReturn(Optional.empty());
        given(userMapper.toEntity(registerRequest)).willReturn(user);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        authService.register(registerRequest);


        assertThat(user.getLoggedAt()).isNotNull();
        verify(passwordEncoder).encode("password");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void register_shouldSaveRefreshToken_inTokenService() {
        given(userRepository.findByEmail("test@mail.com")).willReturn(Optional.empty());
        given(userMapper.toEntity(registerRequest)).willReturn(user);
        given(passwordEncoder.encode("password")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(userId);
            return u;
        });
        given(jwtService.generateAccessToken(any(UUID.class), eq("test@mail.com"))).willReturn("access");
        given(jwtService.generateRefreshToken(any(UUID.class), eq("test@mail.com"))).willReturn("refresh");
        given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

        authService.register(registerRequest);

        assertThat(user.getLoggedAt()).isNotNull();
        verify(tokenService).saveRefreshToken(userId, "refresh", 7L);
    }

    @Test
    void register_shouldSendUserCreatedEvent_afterSuccessfulRegistration() {
        mockSuccessfulRegistration();

        authService.register(registerRequest);

        ArgumentCaptor<UserCreatedEvent> captor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(userEventProducer).sendUserCreated(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(userId);
    }

    @Test
    void login_shouldReturnJwtTokens_whenCredentialsAreValid() {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(jwtService.generateAccessToken(any(UUID.class), eq("test@mail.com"))).willReturn("access");
        given(jwtService.generateRefreshToken(any(UUID.class), eq("test@mail.com"))).willReturn("refresh");
        given(jwtProperties.getAccessTokenExpirationMinutes()).willReturn(15L);
        given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

        JwtTokens tokens = authService.login(loginRequest);

        assertThat(savedUser.getLoggedAt()).isNotNull();
        assertThat(tokens.getAccessToken()).isEqualTo("access");
        assertThat(tokens.getRefreshToken()).isEqualTo("refresh");
        assertThat(tokens.getAccessTokenExpiresIn()).isEqualTo(15L);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("e"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_shouldSaveRefreshToken_whenLoginSuccess() {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);

        given(jwtService.generateAccessToken(eq(savedUser.getId()), eq("test@mail.com"))).willReturn("access");
        given(jwtService.generateRefreshToken(eq(savedUser.getId()), eq("test@mail.com"))).willReturn("refresh");
        given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

        authService.login(loginRequest);

        verify(tokenService).saveRefreshToken(eq(savedUser.getId()), eq("refresh"), eq(7L));
        assertThat(savedUser.getLoggedAt()).isNotNull();
    }

    @Test
    void login_shouldThrowRunTimeException_whenLoginFailure() {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("e"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void refreshToken_shouldReturnNewJwtTokens_whenTokenIsValid() {
        DecodedJWT jwt = mock(DecodedJWT.class);
        given(jwtService.verify(any(String.class))).willReturn(jwt);
        given(jwtService.isRefreshToken(jwt)).willReturn(true);
        given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
        given(jwtService.getEmailFromVerifiedToken(jwt)).willReturn("test@mail.com");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(tokenService.getRefreshToken(userId)).willReturn("refresh");
        given(jwtService.generateAccessToken(any(UUID.class), eq("test@mail.com"))).willReturn("access");
        given(jwtService.generateRefreshToken(any(UUID.class), eq("test@mail.com"))).willReturn("new refresh");
        given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

        JwtTokens tokens = authService.refreshToken("refresh");
        verify(tokenService).saveRefreshToken(userId, "new refresh", 7L);
        assertThat(tokens.getAccessToken()).isEqualTo("access");
        assertThat(tokens.getRefreshToken()).isEqualTo("new refresh");
    }

    @Test
    void refreshToken_shouldThrowInvalidRefreshTokenException_whenTokenIsInvalid() {
        DecodedJWT jwt = mock(DecodedJWT.class);
        given(jwtService.verify(any(String.class))).willReturn(jwt);
        given(jwtService.isRefreshToken(jwt)).willReturn(true);
        given(jwtService.getUserIdFromVerifiedToken(any(DecodedJWT.class))).willReturn(userId);
        given(jwtService.getEmailFromVerifiedToken(any(DecodedJWT.class))).willReturn("test@mail.com");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(tokenService.getRefreshToken(userId)).willReturn(null);

        assertThatThrownBy(() -> authService.refreshToken("refresh"))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void refreshToken_shouldThrowInvalidRefreshTokenException_whenTokenDoesNotMatchStored() {
        DecodedJWT jwt = mock(DecodedJWT.class);
        given(jwtService.verify(any(String.class))).willReturn(jwt);
        given(jwtService.isRefreshToken(jwt)).willReturn(true);
        given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
        given(jwtService.getEmailFromVerifiedToken(jwt)).willReturn("test@mail.com");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(tokenService.getRefreshToken(userId)).willReturn("correct refresh");

        assertThatThrownBy(() -> authService.refreshToken("refresh"))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void refreshToken_shouldSaveNewRefreshToken_inTokenService() {
        DecodedJWT jwt = mock(DecodedJWT.class);
        given(jwtService.verify(any(String.class))).willReturn(jwt);
        given(jwtService.isRefreshToken(jwt)).willReturn(true);
        given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
        given(jwtService.getEmailFromVerifiedToken(jwt)).willReturn("test@mail.com");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(tokenService.getRefreshToken(userId)).willReturn("refresh");
        given(jwtService.generateAccessToken(any(UUID.class), eq("test@mail.com"))).willReturn("access");
        given(jwtService.generateRefreshToken(any(UUID.class), eq("test@mail.com"))).willReturn("new refresh");
        given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);

        authService.refreshToken("refresh");

        verify(tokenService).saveRefreshToken(userId, "new refresh", 7L);
    }

    @Test
    void refreshToken_shouldThrowInvalidRefreshToken_whenUserDoesNotExist() {
        DecodedJWT jwt = mock(DecodedJWT.class);
        given(jwtService.verify(any(String.class))).willReturn(jwt);
        given(jwtService.isRefreshToken(jwt)).willReturn(true);
        given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
        given(jwtService.getEmailFromVerifiedToken(jwt)).willReturn("test@mail.com");
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken("refresh"))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void refreshToken_shouldThrowException_whenJwtServiceFails() {
        given(jwtService.verify(any(String.class)))
                .willThrow(new JWTVerificationException("Unauthorized"));

        assertThatThrownBy(() -> authService.refreshToken("badToken"))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("Unauthorized");

        verify(tokenService, never()).saveRefreshToken(any(), any(), anyLong());
    }

    @Test
    void logout_shouldDeleteRefreshToken_fromTokenService() {
        DecodedJWT jwt = mock(DecodedJWT.class);
        given(jwtService.verify(anyString())).willReturn(jwt);
        given(jwtService.isRefreshToken(jwt)).willReturn(true);
        given(jwtService.getUserIdFromVerifiedToken(jwt)).willReturn(userId);
        doNothing().when(tokenService).deleteRefreshToken(userId);

        authService.logout("refresh");

        verify(tokenService).deleteRefreshToken(userId);
    }

    @Test
    void deleteUser_shouldDeleteUser_andRefreshToken_whenUserExists() {
        authService.deleteUser(new UserDeletedEvent(userId));

        verify(userRepository).deleteById(userId);
        verify(tokenService).deleteRefreshToken(userId);
    }

    private void mockSuccessfulRegistration() {
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userMapper.toEntity(any(RegisterRequest.class))).willReturn(user);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(userId);
            return u;
        });
        given(jwtService.generateAccessToken(any(UUID.class), eq("test@mail.com"))).willReturn("access");
        given(jwtService.generateRefreshToken(any(UUID.class), eq("test@mail.com"))).willReturn("refresh");
        given(jwtProperties.getAccessTokenExpirationMinutes()).willReturn(15L);
        given(jwtProperties.getRefreshTokenExpirationDays()).willReturn(7L);
    }
}

package korobkin.nikita.auth_service.service.impl;

import jakarta.transaction.Transactional;
import korobkin.nikita.auth_service.config.JwtProperties;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RefreshTokenRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.dto.response.JwtResponse;
import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.exception.InvalidCredentialsException;
import korobkin.nikita.auth_service.exception.InvalidRefreshTokenException;
import korobkin.nikita.auth_service.exception.UserAlreadyExistsException;
import korobkin.nikita.auth_service.kafka.producer.UserEventProducer;
import korobkin.nikita.auth_service.mapper.UserMapper;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.security.JwtService;
import korobkin.nikita.auth_service.security.UserDetailsImpl;
import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.auth_service.service.TokenCacheService;
import korobkin.nikita.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenCacheService tokenService;
    private final JwtProperties jwtProperties;
    private final UserEventProducer userEventProducer;

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.warn("Registration failed: email {} is already taken", registerRequest.getEmail());
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setLoggedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User registered successfully: userId={}, email={}", user.getId(), user.getEmail());

        String access = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        tokenService.saveRefreshToken(
                user.getId(),
                refresh,
                jwtProperties.getRefreshTokenExpirationDays()
        );

        userEventProducer.sendUserCreated(new UserCreatedEvent(user.getId()));

        return new JwtResponse(access, refresh, jwtProperties.getAccessTokenExpirationMinutes());
    }

    @Override
    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();

            log.info("User logged in: userId={}, email={}", user.getId(), user.getEmail());

            String access = jwtService.generateAccessToken(user.getId(), user.getEmail());
            String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());

            tokenService.saveRefreshToken(
                    user.getId(),
                    refresh,
                    jwtProperties.getRefreshTokenExpirationDays()
            );

            return new JwtResponse(access, refresh, jwtProperties.getAccessTokenExpirationMinutes());
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for email: {}", loginRequest.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (Exception e) {
            log.error("Unexpected login error for email: {}", loginRequest.getEmail(), e);
            throw new RuntimeException("Login failed", e);
        }
    }

    public void logout(RefreshTokenRequest request) {
        UUID userId = jwtService.getUserIdFromToken(request.getRefreshToken());
        tokenService.deleteRefreshToken(userId);
        log.info("User logged out: userId={}", userId);
    }

    public JwtResponse refreshToken(RefreshTokenRequest request) {
        UUID userId = jwtService.getUserIdFromToken(request.getRefreshToken());
        String email = jwtService.getEmailFromToken(request.getRefreshToken());

        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRefreshTokenException("User not found"));

        String storedRefresh = tokenService.getRefreshToken(userId);
        if (storedRefresh == null || !storedRefresh.equals(request.getRefreshToken())) {
            log.warn("Invalid refresh token for userId={}, email={}", userId, email);
            throw new InvalidRefreshTokenException("Invalid or expired refresh token");
        }

        String access = jwtService.generateAccessToken(userId, email);
        String newRefresh = jwtService.generateRefreshToken(userId, email);

        tokenService.saveRefreshToken(
                userId,
                newRefresh,
                jwtProperties.getRefreshTokenExpirationDays()
        );

        log.info("Refresh token updated for userId={}, email={}", userId, email);

        return new JwtResponse(
                access,
                newRefresh,
                jwtProperties.getAccessTokenExpirationMinutes()
        );
    }
}

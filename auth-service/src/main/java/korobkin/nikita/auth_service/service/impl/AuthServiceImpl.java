package korobkin.nikita.auth_service.service.impl;

import jakarta.transaction.Transactional;
import korobkin.nikita.auth_service.config.JwtProperties;
import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.exception.*;
import korobkin.nikita.auth_service.kafka.producer.UserEventProducer;
import korobkin.nikita.auth_service.mapper.UserMapper;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.security.JwtService;
import korobkin.nikita.auth_service.security.UserDetailsImpl;
import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.auth_service.service.TokenCacheService;
import korobkin.nikita.events.UserCreatedEvent;
import korobkin.nikita.events.UserDeletedEvent;
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
    public JwtTokens register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.warn("Registration failed: email {} is already taken", registerRequest.getEmail());
            throw new EmailAlreadyExistsException(ErrorCode.EMAIL_EXISTS);
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

        return new JwtTokens(access, refresh, jwtProperties.getAccessTokenExpirationMinutes());
    }

    @Override
    @Transactional
    public JwtTokens login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();
            user.setLoggedAt(LocalDateTime.now());

            log.info("User logged in: userId={}, email={}", user.getId(), user.getEmail());

            String access = jwtService.generateAccessToken(user.getId(), user.getEmail());
            String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());

            tokenService.saveRefreshToken(
                    user.getId(),
                    refresh,
                    jwtProperties.getRefreshTokenExpirationDays()
            );

            return new JwtTokens(access, refresh, jwtProperties.getAccessTokenExpirationMinutes());
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for email: {}", loginRequest.getEmail());
            throw new InvalidCredentialsException(ErrorCode.INVALID_CREDENTIALS);
        } catch (Exception e) {
            log.error("Unexpected login error for email: {}", loginRequest.getEmail(), e);
            throw new AuthenticationProcessingException(ErrorCode.INTERNAL_ERROR, e);
        }
    }

    public void logout(String refreshToken) {
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        tokenService.deleteRefreshToken(userId);
        log.info("User logged out: userId={}", userId);
    }

    @Override
    @Transactional
    public void deleteUser(UserDeletedEvent userDeletedEvent) {
        userRepository.deleteById(userDeletedEvent.userId());
        log.info("User deleted successfully: userId={}", userDeletedEvent.userId());
        tokenService.deleteRefreshToken(userDeletedEvent.userId());
    }

    public JwtTokens refreshToken(String refreshToken) {
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        String email = jwtService.getEmailFromToken(refreshToken);

        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRefreshTokenException(ErrorCode.TOKEN_INVALID));

        String storedRefresh = tokenService.getRefreshToken(userId);
        if (storedRefresh == null || !storedRefresh.equals(refreshToken)) {
            log.warn("Invalid refresh token for userId={}, email={}", userId, email);
            throw new InvalidRefreshTokenException(ErrorCode.TOKEN_INVALID);
        }

        String access = jwtService.generateAccessToken(userId, email);
        String newRefresh = jwtService.generateRefreshToken(userId, email);

        tokenService.saveRefreshToken(
                userId,
                newRefresh,
                jwtProperties.getRefreshTokenExpirationDays()
        );

        log.info("Refresh token updated for userId={}, email={}", userId, email);

        return new JwtTokens(
                access,
                newRefresh,
                jwtProperties.getAccessTokenExpirationMinutes()
        );
    }
}

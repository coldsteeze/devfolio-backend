package korobkin.nikita.auth_service.security.jwt.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import korobkin.nikita.auth_service.security.jwt.JwtProperties;
import korobkin.nikita.auth_service.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final String ACCESS_TOKEN_TYPE = "access_token";
    private static final String REFRESH_TOKEN_TYPE = "refresh_token";

    private final JwtProperties jwtProperties;


    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtProperties.getSecret());
    }

    private JWTVerifier getVerifier() {
        return JWT.require(getAlgorithm())
                .withIssuer(jwtProperties.getIssuer())
                .build();
    }

    @Override
    public String generateAccessToken(UUID userId, String email, String userRole) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(jwtProperties.getAccessTokenExpirationMinutes()));

        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("type", ACCESS_TOKEN_TYPE)
                .withClaim("role", userRole)
                .withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(getAlgorithm());
    }

    @Override
    public String generateRefreshToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofDays(jwtProperties.getRefreshTokenExpirationDays()));

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("type", REFRESH_TOKEN_TYPE)
                .withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(getAlgorithm());
    }

    @Override
    public DecodedJWT verify(String token) throws JWTVerificationException {
        return getVerifier().verify(token);
    }

    @Override
    public boolean isAccessToken(DecodedJWT jwt) throws JWTVerificationException {
        String type = jwt.getClaim("type").asString();
        return ACCESS_TOKEN_TYPE.equals(type);
    }

    @Override
    public boolean isRefreshToken(DecodedJWT jwt) throws JWTVerificationException {
        String type = jwt.getClaim("type").asString();
        return REFRESH_TOKEN_TYPE.equals(type);
    }

    @Override
    public String getEmailFromVerifiedToken(DecodedJWT jwt) {
        return jwt.getClaim("email").asString();
    }

    @Override
    public UUID getUserIdFromVerifiedToken(DecodedJWT jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}

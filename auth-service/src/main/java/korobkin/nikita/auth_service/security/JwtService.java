package korobkin.nikita.auth_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import korobkin.nikita.auth_service.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtProperties.getSecret());
    }

    private JWTVerifier getVerifier() {
        return JWT.require(getAlgorithm())
                .withIssuer(jwtProperties.getIssuer())
                .build();
    }

    public String generateAccessToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(jwtProperties.getAccessTokenExpirationMinutes()));

        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("type", "access_token")
                .withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(getAlgorithm());
    }

    public String generateRefreshToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofDays(jwtProperties.getRefreshTokenExpirationDays()));

        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("type", "refresh_token")
                .withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(getAlgorithm());
    }

    public String getEmailFromToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = getVerifier().verify(token);
        return jwt.getClaim("email").asString();
    }

    public UUID getUserIdFromToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = getVerifier().verify(token);
        return UUID.fromString(jwt.getSubject());
    }
}

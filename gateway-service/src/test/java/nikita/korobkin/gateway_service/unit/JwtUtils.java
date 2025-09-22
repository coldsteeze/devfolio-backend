package nikita.korobkin.gateway_service.unit;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import nikita.korobkin.gateway_service.config.JwtProperties;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(1));

        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withClaim("type", "access_token")
                .withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }
}

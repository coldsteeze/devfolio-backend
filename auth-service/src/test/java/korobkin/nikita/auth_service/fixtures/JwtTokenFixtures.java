package korobkin.nikita.auth_service.fixtures;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import korobkin.nikita.auth_service.security.jwt.JwtProperties;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.UUID;

@UtilityClass
public class JwtTokenFixtures {

    public static final String INVALID_TOKEN = "invalid-token";

    public static final String DEFAULT_ACCESS_TOKEN = "access";
    public static final String DEFAULT_REFRESH_TOKEN = "refresh";
    public static final long DEFAULT_EXPIRES_IN = 15;

    public static final String NEW_ACCESS_TOKEN = "new-access-token";
    public static final String NEW_REFRESH_TOKEN = "refresh";

    public static JwtTokens jwtTokens(String access, String refresh, long expires) {
        return new JwtTokens(access, refresh, expires);
    }

    public static JwtTokens jwtTokens() {
        return jwtTokens(DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN, DEFAULT_EXPIRES_IN);
    }

    public static JwtTokens newJwtTokens() {
        return jwtTokens(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN, DEFAULT_EXPIRES_IN);
    }

    public static String createValidRefreshToken(JwtProperties jwtProperties) {
        return JWT.create()
                .withSubject(UUID.randomUUID().toString())
                .withClaim("type", "refresh_token")
                .withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }

    public static String createExpiredRefreshToken(JwtProperties jwtProperties) {
        return JWT.create()
                .withSubject(UUID.randomUUID().toString())
                .withClaim("type", "refresh_token")
                .withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000))
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }
}

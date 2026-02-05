package korobkin.nikita.auth_service.fixtures;

import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtTokenFixtures {

    public static final String INVALID_TOKEN = "invalid-token";

    public static final String DEFAULT_ACCESS_TOKEN = "access";
    public static final String DEFAULT_REFRESH_TOKEN = "refresh";
    public static final int DEFAULT_EXPIRES_IN = 15;

    public static JwtTokens jwtTokens(String access, String refresh, int expires) {
        return new JwtTokens(access, refresh, expires);
    }

    public static JwtTokens jwtTokens() {
        return jwtTokens(DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN, DEFAULT_EXPIRES_IN);
    }
}

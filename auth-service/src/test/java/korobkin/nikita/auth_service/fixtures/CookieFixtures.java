package korobkin.nikita.auth_service.fixtures;

import jakarta.servlet.http.Cookie;
import korobkin.nikita.auth_service.config.AuthCookieProperties;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieFixtures {

    public static final String VALID_NAME = "refreshToken";
    public static final String INVALID_NAME = "wrongCookie";

    public static AuthCookieProperties authCookieProperties() {
        AuthCookieProperties properties = new AuthCookieProperties();
        properties.setName("refreshToken");
        properties.setHttpOnly(true);
        properties.setSecure(true);
        properties.setSameSite("None");
        properties.setPath("/api/auth");
        properties.setMaxAgeDays(7);

        return properties;
    }

    public static Cookie cookie(String name, String value) {
        return new Cookie(name, value);
    }
}

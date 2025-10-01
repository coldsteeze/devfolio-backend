package korobkin.nikita.auth_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth.cookie")
@Getter
@Setter
public class AuthCookieProperties {

    private String name;
    private boolean httpOnly;
    private boolean secure;
    private String sameSite;
    private String path;
    private long maxAgeDays;
}

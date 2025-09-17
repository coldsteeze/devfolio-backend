package korobkin.nikita.user_profile_service.unit;

import korobkin.nikita.user_profile_service.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

public class TestSecurityUtils {
    public static RequestPostProcessor userPrincipal(UserPrincipal principal) {
        return request -> {
            var auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
            return request;
        };
    }
}

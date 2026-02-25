package korobkin.nikita.skill_service.security;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {

    public boolean hasRole(String role) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}


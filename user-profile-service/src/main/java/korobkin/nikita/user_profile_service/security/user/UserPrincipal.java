package korobkin.nikita.user_profile_service.security.user;

import java.util.UUID;

public record UserPrincipal(UUID userId, String email) {
}

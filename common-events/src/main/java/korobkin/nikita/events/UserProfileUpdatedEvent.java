package korobkin.nikita.events;

import java.util.UUID;

public record UserProfileUpdatedEvent(
        UUID userId,
        String nickname,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        UserType userType
) {
}

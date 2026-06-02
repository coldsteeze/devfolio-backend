package korobkin.nikita.events;

import java.util.UUID;

public record UserProfileAvatarUpdatedEvent(
        UUID eventId,
        UUID userId,
        String avatarUrl
) {
}

package korobkin.nikita.events;

import java.util.UUID;

public record ProjectUpdatedEvent(
        UUID eventId,
        UUID projectId,
        UUID userId,
        String name,
        String shortDescription,
        String description,
        String githubUrl,
        String mainImageUrl,
        boolean projectPublic
) {
}

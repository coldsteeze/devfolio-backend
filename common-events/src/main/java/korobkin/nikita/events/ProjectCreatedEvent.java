package korobkin.nikita.events;

import java.util.UUID;

public record ProjectCreatedEvent(
        UUID eventId,
        UUID projectId,
        UUID userId,
        String name,
        String description,
        String githubUrl,
        boolean projectPublic
) {
}

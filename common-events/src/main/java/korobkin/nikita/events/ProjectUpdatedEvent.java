package korobkin.nikita.events;

import java.util.UUID;

public record ProjectUpdatedEvent(
        UUID projectId,
        String name,
        String description,
        String githubUrl,
        boolean isPublic
) {
}

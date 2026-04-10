package korobkin.nikita.events;

import java.util.UUID;

public record ProjectCreatedEvent(
        UUID projectId,
        String name,
        String description,
        String githubUrl,
        boolean isPublic
) {
}

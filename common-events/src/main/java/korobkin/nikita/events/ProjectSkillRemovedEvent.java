package korobkin.nikita.events;

import java.util.UUID;

public record ProjectSkillRemovedEvent(
        UUID eventId,
        UUID projectId,
        String name
) {
}

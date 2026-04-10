package korobkin.nikita.events;

import java.util.UUID;

public record ProjectSkillRemovedEvent(
        UUID projectId,
        String name
) {
}

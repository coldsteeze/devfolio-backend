package korobkin.nikita.events;

import java.util.UUID;

public record ProjectSkillVerificationCompletedEvent(UUID projectId, UUID skillId, boolean confirmed) {
}

package korobkin.nikita.events;

import java.util.UUID;

public record ProjectSkillVerificationRequestedEvent(
        UUID projectId, UUID skillId, String skillName, String githubUrl) {
}

package korobkin.nikita.events;

import korobkin.nikita.events.skill.SkillShortInfo;

import java.util.List;
import java.util.UUID;

public record ProjectSkillVerificationRequestedEvent(
        UUID eventId,
        UUID projectId,
        String githubUrl,
        List<SkillShortInfo> skills
) {
}

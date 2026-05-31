package korobkin.nikita.events;

import korobkin.nikita.events.skill.SkillVerificationResult;

import java.util.List;
import java.util.UUID;

public record ProjectSkillVerificationCompletedEvent(
        UUID eventId,
        UUID projectId,
        List<SkillVerificationResult> results
) {}

package korobkin.nikita.events.skill;

import java.util.UUID;

public record SkillVerificationResult(
        UUID projectSkillId,
        boolean confirmed
) {}

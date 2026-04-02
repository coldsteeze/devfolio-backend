package korobkin.nikita.events.skill;

import java.util.UUID;

public record SkillShortInfo(
        UUID projectSkillId,
        UUID skillId,
        String name,
        SkillCategory category
) {}

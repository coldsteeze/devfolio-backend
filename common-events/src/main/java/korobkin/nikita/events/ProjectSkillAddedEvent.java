package korobkin.nikita.events;

import korobkin.nikita.events.skill.SkillCategory;

import java.util.UUID;

public record ProjectSkillAddedEvent(
        UUID eventId,
        UUID projectId,
        String skillName,
        SkillCategory skillCategory
) {
}

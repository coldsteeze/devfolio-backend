package korobkin.nikita.project_service.dto.response;

import korobkin.nikita.project_service.entity.enums.SkillCategory;

import java.util.UUID;

public record ProjectSkillResponse(
        UUID skillId,
        String skillName,
        SkillCategory skillCategory,
        boolean confirmed,
        boolean manuallyAdded
) {
}

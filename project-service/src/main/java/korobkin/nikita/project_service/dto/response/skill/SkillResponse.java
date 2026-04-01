package korobkin.nikita.project_service.dto.response.skill;

import korobkin.nikita.project_service.entity.enums.SkillCategory;

import java.util.UUID;

public record SkillResponse(UUID id, String name, SkillCategory category) {
}

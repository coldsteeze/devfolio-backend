package korobkin.nikita.project_service.dto.response.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.project_service.entity.enums.SkillCategory;

import java.util.UUID;

@Schema(description = "Response DTO representing a skill")
public record SkillResponse(

        @Schema(example = "550e8400-e29b-41d4-a716-446655440000", description = "Skill unique identifier")
        UUID id,

        @Schema(example = "Java", description = "Skill name")
        String name,

        @Schema(example = "LANGUAGE", description = "Skill category")
        SkillCategory category
) {
}

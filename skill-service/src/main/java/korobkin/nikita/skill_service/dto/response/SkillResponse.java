package korobkin.nikita.skill_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.skill_service.entity.enums.SkillCategory;

import java.util.UUID;

public record SkillResponse(

        @Schema(example = "123e4567-e89b-12d3-a456-426614174000", description = "Unique skill identifier")
        UUID id,

        @Schema(example = "Java", description = "Field for skill name")
        String name,

        @Schema(
                example = "LANGUAGE",
                description = "Type of skill (e.g., FRAMEWORK, TOOL, PLATFORM)"
        )
        SkillCategory category
) {
}

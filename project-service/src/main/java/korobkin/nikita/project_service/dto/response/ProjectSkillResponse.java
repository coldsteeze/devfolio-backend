package korobkin.nikita.project_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.project_service.entity.enums.SkillCategory;

import java.util.UUID;

@Schema(description = "Response DTO representing a project skill")
public record ProjectSkillResponse(

        @Schema(example = "550e8400-e29b-41d4-a716-446655440000", description = "Skill ID")
        UUID skillId,

        @Schema(example = "Spring Boot", description = "Skill name")
        String skillName,

        @Schema(example = "FRAMEWORK", description = "Skill category")
        SkillCategory skillCategory,

        @Schema(example = "true", description = "Indicates if the skill is confirmed")
        boolean confirmed,

        @Schema(example = "false", description = "Indicates if the skill was manually added")
        boolean manuallyAdded
) {
}

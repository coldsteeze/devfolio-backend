package korobkin.nikita.portfolio_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.portfolio_service.entity.enums.SkillCategory;

@Schema(description = "Skill used in a project")
public record PortfolioProjectSkillResponse(

        @Schema(
                description = "Skill name",
                example = "Java"
        )
        String skillName,

        @Schema(
                description = "Skill category (e.g. BACKEND, FRONTEND, DEVOPS, etc.)",
                example = "BACKEND"
        )
        SkillCategory skillCategory,

        @Schema(
                description = "Indicates whether the skill is confirmed or verified",
                example = "true"
        )
        boolean confirmed
) {}
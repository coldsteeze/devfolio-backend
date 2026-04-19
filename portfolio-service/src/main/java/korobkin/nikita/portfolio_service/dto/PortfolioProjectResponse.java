package korobkin.nikita.portfolio_service.dto;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Portfolio project information")
public record PortfolioProjectResponse(

        @Schema(
                description = "Project unique identifier",
                example = "1a7b2c3d-9e8f-4a6b-8c7d-123456789abc"
        )
        UUID projectId,

        @Schema(
                description = "Project name",
                example = "Portfolio Service"
        )
        String name,

        @Schema(
                description = "Project description",
                example = "Microservice for managing user portfolios with Spring Boot"
        )
        String description,

        @Schema(
                description = "URL to GitHub repository",
                example = "https://github.com/user/portfolio-service"
        )
        String githubUrl,

        @Schema(
                description = "Indicates whether the project is public",
                example = "true"
        )
        boolean projectPublic,

        @Schema(description = "List of skills used in the project")
        List<PortfolioProjectSkillResponse> skills
) {}
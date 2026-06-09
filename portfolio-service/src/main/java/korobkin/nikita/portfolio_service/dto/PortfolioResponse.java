package korobkin.nikita.portfolio_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Response containing user's portfolio information")
public record PortfolioResponse(

        @Schema(
                description = "User unique identifier",
                example = "3f2a9c6e-8d41-4c7b-9c2e-1f6b8d4a7e90"
        )
        UUID userId,

        @Schema(
                description = "Unique username (nickname)",
                example = "nick_dev"
        )
        String nickname,

        @Schema(
                description = "User first name",
                example = "Nick"
        )
        String firstName,

        @Schema(
                description = "User last name",
                example = "Korobkin"
        )
        String lastName,

        @Schema(
                description = "Short user bio or description",
                example = "Java backend developer focused on Spring Boot microservices"
        )
        String bio,

        @Schema(
                description = "Total number of projects in the portfolio",
                example = "3"
        )
        short totalProjects,

        @Schema(
                description = "List of user projects"
        )
        List<PortfolioProjectResponse> projects,

        @Schema(
                description = "Work experience and education history"
        )
        List<PortfolioCareerEntryResponse> career
) {}

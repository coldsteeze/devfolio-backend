package korobkin.nikita.project_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO representing a project")
public record ProjectResponse(

        @Schema(example = "550e8400-e29b-41d4-a716-446655440000", description = "Project unique identifier")
        UUID id,

        @Schema(example = "Portfolio Platform", description = "Project name")
        String name,

        @Schema(example = "Platform for developers to showcase projects", description = "Project description")
        String description,

        @Schema(example = "661b8411-e29b-41d4-a716-44665544123124", description = "User unique identifier")
        UUID userId,

        @Schema(description = "Short project description for preview",
                example = "Platform for developers to showcase projects")
        String shortDescription,

        @Schema(description = "URL of the main project image",
                example = "https://cdn.example.com/projects/123/main.jpg")
        String mainImageUrl,

        @Schema(example = "https://github.com/user/project", description = "GitHub repository URL")
        String githubUrl,

        @Schema(example = "true", description = "Project visibility (true = public, false = private)")
        boolean projectPublic,

        @Schema(example = "1", description = "Count views project")
        Long viewsCount,

        @Schema(example = "1", description = "Count likes project")
        Long likesCount,

        @Schema(example = "2026-03-01T12:00:00", description = "Project creation timestamp")
        LocalDateTime createdAt,

        @Schema(example = "2026-03-10T15:30:00", description = "Project last update timestamp")
        LocalDateTime updatedAt
) {
}

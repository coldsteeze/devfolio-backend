package korobkin.nikita.project_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO representing project in feed")
public record ProjectFeedResponse(

        @Schema(description = "Project unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Project name",
                example = "Portfolio Platform")
        String name,

        @Schema(description = "Short project description for preview",
                example = "Platform for developers to showcase projects")
        String shortDescription,

        @Schema(description = "URL of the main project image",
                example = "https://cdn.example.com/projects/123/main.jpg")
        String mainImageUrl,

        @Schema(description = "Project creation timestamp",
                example = "2026-03-01T12:00:00")
        LocalDateTime createdAt
) {}

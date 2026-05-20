package korobkin.nikita.project_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO representing a project in user's favorites list")
public record ProjectFavoriteResponse(

        @Schema(
                description = "Unique identifier of the favorite record",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID favoriteId,

        @Schema(
                description = "Identifier of the favorited project",
                example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
        )
        UUID projectId,

        @Schema(
                description = "Date and time when the project was added to favorites",
                example = "2026-05-19T14:30:00",
                format = "date-time"
        )
        LocalDateTime createdAt
) {
}

package korobkin.nikita.project_service.dto.response.media;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing uploaded file URL")
public record MediaResponse(

        @Schema(
                description = "Public URL of uploaded file",
                example = "http://localhost:9000/media-bucket/avatars/123e4567-e89b-12d3-a456-426614174000.png",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String url
) {}

package korobkin.nikita.media_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to delete a media file by URL")
public record DeleteMediaRequest(

        @Schema(
                description = "Full URL of the file to delete",
                example = "http://localhost:9000/media-bucket/avatars/123e4567-e89b-12d3-a456-426614174000.png",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String url
) {}

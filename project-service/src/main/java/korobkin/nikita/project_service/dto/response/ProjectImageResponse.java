package korobkin.nikita.project_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Project image response")
public record ProjectImageResponse(
        @Schema(
                description = "Unique identifier of the image",
                example = "a1b2c3d4-1234-5678-9012-abcdefabcdef"
        )
        UUID id,

        @Schema(
                description = "Identifier of the project this image belongs to",
                example = "d85179ce-72e2-4318-955c-d61dab80ff8b"
        )
        UUID projectId,

        @Schema(
                description = "Public URL of the image stored in object storage",
                example = "http://localhost:9000/devfolio/projects/images/a1.png"
        )
        String imageUrl
) {}

package korobkin.nikita.project_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Project like status response")
public record LikeStatusResponse(

        @Schema(
                description = "Indicates whether the current user liked the project",
                example = "true"
        )
        Boolean liked

) {
}

package korobkin.nikita.project_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.project_service.dto.response.LikeStatusResponse;
import korobkin.nikita.project_service.exception.ApiError;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(
        name = "Project Interaction",
        description = "Project views and likes management"
)
public interface ProjectInteractionControllerDocs {

    @Operation(
            summary = "Record project view",
            description = "Records a view for the specified project",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Project view recorded successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Project with this id not found",
                                              "code": "PROJECT_NOT_FOUND",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/view",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
            }
    )
    void recordView(
            @Parameter(
                    description = "Project ID",
                    required = true,
                    schema = @Schema(format = "uuid")
            )
            UUID projectId,
            UserPrincipal currentUser
    );

    @Operation(
            summary = "Like project",
            description = "Adds project to user favorites",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Project liked successfully"
                    )
            }
    )
    void like(
            @Parameter(
                    description = "Project ID",
                    required = true,
                    schema = @Schema(format = "uuid")
            )
            UUID projectId,
            UserPrincipal currentUser
    );

    @Operation(
            summary = "Remove project like",
            description = "Removes project from user favorites",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Project like removed successfully"
                    )
            }
    )
    void unlike(
            @Parameter(
                    description = "Project ID",
                    required = true,
                    schema = @Schema(format = "uuid")
            )
            UUID projectId,
            UserPrincipal currentUser
    );

    @Operation(
            summary = "Get project like status",
            description = "Checks whether the current user liked the project",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Like status fetched successfully"
                    )
            }
    )
    ResponseEntity<LikeStatusResponse> getLikeStatus(
            @Parameter(
                    description = "Project ID",
                    required = true,
                    schema = @Schema(format = "uuid")
            )
            UUID projectId,
            UserPrincipal currentUser
    );
}

package korobkin.nikita.user_profile_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.user_profile_service.dto.request.UpdateCareerRequest;
import korobkin.nikita.user_profile_service.dto.response.CareerResponse;
import korobkin.nikita.user_profile_service.exception.ApiError;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Career", description = "Manage user career: work and education history")
public interface CareerControllerDocs {

    @Operation(
            summary = "Get user career",
            description = "Get career information (work + education) by userId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Career successfully fetched",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CareerResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User profile not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "User profile not found",
                                              "code": "PROFILE_NOT_FOUND",
                                              "path": "/api/profiles/{userId}/career",
                                              "timestamp": "2026-06-07T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<CareerResponse> getCareer(
            @PathVariable @Parameter(
                    description = "User profile ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            UUID userId
    );

    @Operation(
            summary = "Update my career",
            description = "Replace full career history (work + education)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Career successfully updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CareerResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "items[0].title Title is required",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/profiles/me/career",
                                              "timestamp": "2026-06-07T00:00:00"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User profile not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "User profile not found",
                                              "code": "PROFILE_NOT_FOUND",
                                              "path": "/api/profiles/me/career",
                                              "timestamp": "2026-06-07T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<CareerResponse> updateCareer(
            @RequestBody(
                    description = "Replace full career history",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateCareerRequest.class)
                    )
            )
            UpdateCareerRequest request,

            UserPrincipal principal
    );
}

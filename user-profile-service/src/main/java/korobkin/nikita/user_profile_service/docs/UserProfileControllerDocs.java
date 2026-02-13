package korobkin.nikita.user_profile_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.PagedResponse;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.exception.ApiError;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;
import java.util.UUID;

@Tag(name = "User profile", description = "Manage user profiles: get, fill, update, search, delete")
public interface UserProfileControllerDocs {

    @Operation(
            summary = "Get my user profile",
            description = "Get information about user profile",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile successfully fetched",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserProfileResponse.class)
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
                                              "message": "User with this id not found",
                                              "code": "PROFILE_NOT_FOUND",
                                              "path": "/api/profiles/me",
                                              "timestamp": "2026-10-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<UserProfileResponse> getMyProfile(UserPrincipal principal);


    @Operation(
            summary = "Fill user profile",
            description = "Fill user profile and return information about it",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User profile successfully filled",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserProfileResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Nickname already exists",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 409,
                                              "error": "Сonflict",
                                              "message": "Nickname already exists",
                                              "code": "NICKNAME_ALREADY_EXISTS",
                                              "path": "/api/profiles",
                                              "timestamp": "2026-10-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<UserProfileResponse> fillMyProfile(
            @RequestBody(
                    description = "Fill user data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserProfileRequest.class)
                    )
            )
            UpdateUserProfileRequest request,
            UserPrincipal principal
    );

    @Operation(
            summary = "Get user profile",
            description = "Get information about user profile by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile successfully fetched",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserProfileResponse.class)
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
                                              "message": "User with this id not found",
                                              "code": "PROFILE_NOT_FOUND",
                                              "path": "/api/profiles/123e4567-e89b-12d3-a456-426614174000",
                                              "timestamp": "2026-10-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable @Parameter(
                    description = "User profile ID",
                    example = "123e4567-e89b-12d3-a456-426614174000") UUID userId
    );


    @Operation(
            summary = "Update my user profile",
            description = "Update my user profile and return information about it",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile successfully updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserProfileResponse.class)
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
                                              "message": "nickname Nickname is required",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/profiles/me",
                                              "timestamp": "2026-10-02T00:00:00"
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
                                              "message": "User with this id not found",
                                              "code": "PROFILE_NOT_FOUND",
                                              "path": "/api/profiles/me",
                                              "timestamp": "2026-10-02T00:00:00"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Nickname already exists",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 409,
                                              "error": "Сonflict",
                                              "message": "Nickname already exists",
                                              "code": "NICKNAME_ALREADY_EXISTS",
                                              "path": "/api/profiles",
                                              "timestamp": "2026-10-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<UserProfileResponse> updateMyProfile(
            @RequestBody(
                    description = "Update my user profile",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserProfileRequest.class)
                    )
            )
            UpdateUserProfileRequest request,
            UserPrincipal principal
    );

    @Operation(
            summary = "Update my user profile avatar",
            description = "Update my user profile avatar and return information about profile",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile avatar successfully updated",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserProfileResponse.class)
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
                                              "message": "avatarUrl Avatar URL must be a valid URL",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/profiles/me/avatar",
                                              "timestamp": "2026-10-02T00:00:00"
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
                                              "message": "User with this id not found",
                                              "code": "PROFILE_NOT_FOUND",
                                              "path": "/api/profiles/me/avatar",
                                              "timestamp": "2026-10-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<UserProfileResponse> updateProfileAvatar(
            @RequestBody(
                    description = "Update my user profile avatar",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserProfileAvatarRequest.class)
                    )
            )
            UpdateUserProfileAvatarRequest request,
            UserPrincipal principal
    );

    @Operation(
            summary = "List profiles with filter skills",
            description = "Fetch paginated profiles with filters skills",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profiles fetched successfully",
                            content = @Content(schema = @Schema(implementation = PagedResponse.class))
                    )
            }
    )
    ResponseEntity<PagedResponse<UserProfileResponse>> searchProfilesBySkills(
            @ParameterObject Set<String> skills,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Delete user profile",
            description = "Delete user profile by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User profile deleted successfully")
            }
    )
    ResponseEntity<Void> deleteMyProfile(UserPrincipal principal);
}

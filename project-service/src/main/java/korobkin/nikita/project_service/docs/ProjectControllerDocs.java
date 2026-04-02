package korobkin.nikita.project_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.dto.response.ProjectDetailsResponse;
import korobkin.nikita.project_service.dto.response.ProjectResponse;
import korobkin.nikita.project_service.dto.response.ProjectSkillResponse;
import korobkin.nikita.project_service.dto.response.VerificationResponse;
import korobkin.nikita.project_service.exception.ApiError;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Project", description = "Manage projects: get, update, create, add and verify skills")
public interface ProjectControllerDocs {

    @Operation(
            summary = "Create project",
            description = "Create project and return information about created project",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Project created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProjectResponse.class)
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
                                              "message": "name: Name must be between 3 and 100 characters",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/projects",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Skill already exists",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "Project with this name already exists",
                                              "code": "PROJECT_ALREADY_EXISTS",
                                              "path": "/api/projects",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<ProjectResponse> createProject(
            @RequestBody(
                    description = "Create project",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateProjectRequest.class)
                    )
            ) CreateProjectRequest request, UserPrincipal principal
    );

    @Operation(
            summary = "Update project",
            description = "Update project and return information about updated project",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProjectResponse.class)
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
                                              "message": "name: Name must be between 3 and 100 characters",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this project",
                                              "code": "PROJECT_ACCESS_DENIED",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
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
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<ProjectResponse> updateProject(
            @RequestBody(
                    description = "Update project",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateProjectRequest.class)
                    )
            ) UpdateProjectRequest request, @PathVariable UUID projectId, UserPrincipal principal
    );

    @Operation(
            summary = "Get project by id",
            description = "Get project by id and return information about it",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project fetched successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProjectResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this project",
                                              "code": "PROJECT_ACCESS_DENIED",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
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
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<ProjectDetailsResponse> getProject(
            @PathVariable UUID projectId,
            UserPrincipal principal
    );

    @Operation(
            summary = "Delete project by id",
            description = "Delete project by id",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this project",
                                              "code": "PROJECT_ACCESS_DENIED",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
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
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<Void> deleteProject(
            @PathVariable UUID projectId,
            UserPrincipal principal
    );

    @Operation(
            summary = "Add project skill",
            description = "Add project skill id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Add skill to project successfully"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this project",
                                              "code": "PROJECT_ACCESS_DENIED",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/skills/550e8400-e29b-41d4-a716-446655440001",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
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
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/skills/550e8400-e29b-41d4-a716-446655440001",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<ProjectSkillResponse> addSkillProject(
            @PathVariable UUID projectId,
            @PathVariable UUID skillId,
            UserPrincipal principal
    );

    @Operation(
            summary = "Delete project skill",
            description = "Delete project skill id",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Delete project skill successfully"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this project",
                                              "code": "PROJECT_ACCESS_DENIED",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/skills/550e8400-e29b-41d4-a716-446655440001",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
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
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/skills/550e8400-e29b-41d4-a716-446655440001",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<Void> deleteSkillProject(
            @PathVariable UUID projectId,
            @PathVariable UUID skillId,
            UserPrincipal principal
    );

    @Operation(
            summary = "Verify project skills",
            description = "Verify project skills",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Verification request created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = VerificationResponse.class)
                            )),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this project",
                                              "code": "PROJECT_ACCESS_DENIED",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/verifications",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
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
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/verifications",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<VerificationResponse> verifySkillProject(
            @PathVariable UUID projectId,
            UserPrincipal principal
    );

    @Operation(
            summary = "Get project skills",
            description = "Get project skills",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fetched project skills successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProjectSkillResponse.class)
                            )),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "You do not have permission to access this project",
                                              "code": "PROJECT_ACCESS_DENIED",
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/skills",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
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
                                              "path": "/api/projects/550e8400-e29b-41d4-a716-446655440000/skills",
                                              "timestamp": "2026-02-04T00:00:00"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<List<ProjectSkillResponse>> getProjectSkills(
            @PathVariable UUID projectId,
            UserPrincipal principal
    );
}

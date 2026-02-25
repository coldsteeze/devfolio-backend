package korobkin.nikita.skill_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.skill_service.dto.request.BulkSkillRequest;
import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
import korobkin.nikita.skill_service.dto.request.SkillFilterRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.dto.response.PagedResponse;
import korobkin.nikita.skill_service.dto.response.SkillResponse;
import korobkin.nikita.skill_service.exception.ApiError;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Skill", description = "Manage skills: get, update, create, deactivate")
public interface SkillControllerDocs {

    @Operation(
            summary = "List skills with filters",
            description = "Fetch paginated skills with filters",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Skills fetched successfully",
                            content = @Content(schema = @Schema(implementation = PagedResponse.class))
                    )
            }
    )
    ResponseEntity<PagedResponse<SkillResponse>> getSkills(
            @ParameterObject SkillFilterRequest skillFilterRequest,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Get skill",
            description = "Get information about skill by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Skill fetched successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SkillResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Skill not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Skill with this id not found",
                                              "code": "SKILL_NOT_FOUND",
                                              "path": "/api/skills/123e4567-e89b-12d3-a456-426614174000",
                                              "timestamp": "2026-24-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<SkillResponse> getSkill(
            @PathVariable @Parameter(
                    description = "Skill ID",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            ) UUID skillId);


    @Operation(
            summary = "Get skills",
            description = "Get information about skills by ids",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Skill fetched successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SkillResponse.class)
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
                                              "message": "skillIds[0]: Invalid UUID format",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/skills/by-ids",
                                              "timestamp": "2026-24-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<List<SkillResponse>> getBulkSkills(
            @RequestBody(
                    description = "Get bulk skills",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BulkSkillRequest.class)
                    )
            ) BulkSkillRequest request);

    @Operation(
            summary = "Create skill",
            description = "Create skill and return information about created skill",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Skill created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SkillResponse.class)
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
                                              "message": "name: Skill name must be between 2 and 50 characters",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/skills",
                                              "timestamp": "2026-24-02T00:00:00"
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
                                              "message": "Skill already exists",
                                              "code": "SKILL_ALREADY_EXISTS",
                                              "path": "/api/skills",
                                              "timestamp": "2026-24-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<SkillResponse> createSkill(
            @RequestBody(
                    description = "Create skill",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateSkillRequest.class)
                    )
            ) CreateSkillRequest request);

    @Operation(
            summary = "Update skill",
            description = "Update skill and return information about updated skill",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Skill updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SkillResponse.class)
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
                                              "message": "name: Skill name must be between 2 and 50 characters",
                                              "code": "VALIDATION_ERROR",
                                              "path": "/api/skills/123e4567-e89b-12d3-a456-426614174000",
                                              "timestamp": "2026-24-02T00:00:00"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Skill not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Skill with this id not found",
                                              "code": "SKILL_NOT_FOUND",
                                              "path": "/api/skills/123e4567-e89b-12d3-a456-426614174000",
                                              "timestamp": "2026-24-02T00:00:00"
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
                                              "message": "Skill already exists",
                                              "code": "SKILL_ALREADY_EXISTS",
                                              "path": "/api/skills/123e4567-e89b-12d3-a456-426614174000",
                                              "timestamp": "2026-24-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<SkillResponse> updateSkill(
            @PathVariable UUID skillId,
            @RequestBody(
                    description = "Update skill",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateSkillRequest.class)
                    )
            ) UpdateSkillRequest request);

    @Operation(
            summary = "Deactivate skill",
            description = "Deactivate skill and return no content",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Skill deactivated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Skill not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Skill with this id not found",
                                              "code": "SKILL_NOT_FOUND",
                                              "path": "/api/skills/123e4567-e89b-12d3-a456-426614174000",
                                              "timestamp": "2026-24-02T00:00:00"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<Void> deactivateSkill(
            @PathVariable UUID skillId);
}

package korobkin.nikita.media_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import korobkin.nikita.media_service.dto.DeleteMediaRequest;
import korobkin.nikita.media_service.dto.MediaResponse;
import korobkin.nikita.media_service.exception.ApiError;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MediaControllerDocs {

    @Operation(
            summary = "Upload a media photo",
            description = "Uploads a photo",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Photo uploaded successfully",
                            content = @Content(schema = @Schema(implementation = MediaResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request (e.g., empty file)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = {
                                            @ExampleObject(name = "Empty file", value = """
                                                        {
                                                          "status": 400,
                                                          "error": "Bad Request",
                                                          "message": "File is empty",
                                                          "code": "FILE_EMPTY",
                                                          "path": "/api/media/upload",
                                                          "timestamp": "2026-04-30T00:00:00"
                                                        }
                                                    """),
                                            @ExampleObject(name = "Unsupported file type", value = """
                                                        {
                                                          "status": 400,
                                                          "error": "Bad Request",
                                                          "message": "Unsupported file type",
                                                          "code": "INVALID_FILE_TYPE",
                                                          "path": "/api/media/upload",
                                                          "timestamp": "2026-04-30T00:00:00"
                                                        }
                                                    """),
                                            @ExampleObject(name = "Invalid folder", value = """
                                                        {
                                                          "status": 400,
                                                          "error": "Bad Request",
                                                          "message": "Invalid folder",
                                                          "code": "INVALID_FOLDER",
                                                          "path": "/api/media/upload",
                                                          "timestamp": "2026-04-30T00:00:00"
                                                        }
                                                    """)
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "413",
                            description = "Payload Too Large",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = {
                                            @ExampleObject(name = "File size exceeds limit", value = """
                                                        {
                                                          "status": 413,
                                                          "error": "Payload Too Large",
                                                          "message": "File size exceeds limit",
                                                          "code": "FILE_TOO_LARGE",
                                                          "path": "/api/media/upload",
                                                          "timestamp": "2026-04-30T00:00:00"
                                                        }
                                                    """)
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<MediaResponse> upload(
            @Parameter MultipartFile file,
            @Parameter String folder
    );

    @Operation(
            summary = "Delete a media photo",
            description = "Deletes a photo",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Photo deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request (e.g., invalid file url)",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = {
                                            @ExampleObject(name = "Invalid file url", value = """
                                                        {
                                                          "status": 400,
                                                          "error": "Bad Request",
                                                          "message": "Invalid file URL",
                                                          "code": "INVALID_FILE_URL",
                                                          "path": "/api/media",
                                                          "timestamp": "2026-04-30T00:00:00"
                                                        }
                                                    """)
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ResponseEntity<Void> delete(
            @RequestBody(
                    description = "Url for delete photo",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DeleteMediaRequest.class)
                    )
            ) DeleteMediaRequest request);
}

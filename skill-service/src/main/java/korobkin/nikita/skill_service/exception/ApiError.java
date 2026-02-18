package korobkin.nikita.skill_service.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "API error response")
public class ApiError {

    @Schema(example = "404", description = "HTTP status code")
    private int status;

    @Schema(example = "Not Found", description = "Short description of the error")
    private String error;

    @Schema(example = "Skill not found", description = "Detailed error message")
    private String message;

    @Schema(example = "SKILL_NOT_FOUND", description = "Application specific error code")
    private String code;

    @Schema(example = "/api/skills", description = "Path of the endpoint that caused the error")
    private String path;

    @Schema(example = "2026-02-09T00:00:00", description = "Timestamp when the error occurred")
    private LocalDateTime timestamp;
}

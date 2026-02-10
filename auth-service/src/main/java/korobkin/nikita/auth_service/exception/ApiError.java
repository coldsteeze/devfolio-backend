package korobkin.nikita.auth_service.exception;

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

    @Schema(example = "409", description = "HTTP status code")
    private int status;

    @Schema(example = "Bad Request", description = "Short description of the error")
    private String error;

    @Schema(example = "Email already exists", description = "Detailed error message")
    private String message;

    @Schema(example = "EMAIL_ALREADY_EXISTS", description = "Application specific error code")
    private String code;

    @Schema(example = "/api/auth/register", description = "Path of the endpoint that caused the error")
    private String path;

    @Schema(example = "2026-02-09T00:00:00", description = "Timestamp when the error occurred")
    private LocalDateTime timestamp;
}


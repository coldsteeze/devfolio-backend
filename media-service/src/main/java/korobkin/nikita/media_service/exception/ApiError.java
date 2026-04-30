package korobkin.nikita.media_service.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "Standard API error response")
@Builder
public record ApiError(

        @Schema(
                description = "HTTP status code",
                example = "400"
        )
        int status,

        @Schema(
                description = "HTTP status reason",
                example = "Bad Request"
        )
        String error,

        @Schema(
                description = "Human-readable error message",
                example = "Unsupported file type"
        )
        String message,

        @Schema(
                description = "Machine-readable error code",
                example = "INVALID_FILE_TYPE"
        )
        String code,

        @Schema(
                description = "Request path where error occurred",
                example = "/api/media/upload"
        )
        String path,

        @Schema(
                description = "Timestamp of the error",
                example = "2026-04-30T11:45:00",
                type = "string",
                format = "date-time"
        )
        LocalDateTime timestamp
) {}

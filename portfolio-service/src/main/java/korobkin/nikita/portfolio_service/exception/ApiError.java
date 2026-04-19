package korobkin.nikita.portfolio_service.exception;

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

    @Schema(example = "Portfolio not found", description = "Detailed error message")
    private String message;

    @Schema(example = "PORTFOLIO_NOT_FOUND", description = "Application specific error code")
    private String code;

    @Schema(example = "/api/portfolios/3f2a9c6e-8d41-4c7b-9c2e-1f6b8d4a7e90",
            description = "Path of the endpoint that caused the error")
    private String path;

    @Schema(example = "2026-04-19T00:00:00", description = "Timestamp when the error occurred")
    private LocalDateTime timestamp;
}
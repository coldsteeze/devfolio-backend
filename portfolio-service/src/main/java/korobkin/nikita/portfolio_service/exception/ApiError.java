package korobkin.nikita.portfolio_service.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApiError(
        int status,
        String error,
        String message,
        String code,
        String path,
        LocalDateTime timestamp
) {}

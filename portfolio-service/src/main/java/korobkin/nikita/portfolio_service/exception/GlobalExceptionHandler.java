package korobkin.nikita.portfolio_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleAppException(AppException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getStatus(), ex.getMessage(), ex.getErrorCode().name(), request);
    }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String message, String code, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .code(code)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}

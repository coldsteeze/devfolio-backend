package korobkin.nikita.auth_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    EMAIL_EXISTS("Email already exists", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("Invalid email or password", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("Invalid or expired token", HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR("Unexpected server error", HttpStatus.INTERNAL_SERVER_ERROR);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}

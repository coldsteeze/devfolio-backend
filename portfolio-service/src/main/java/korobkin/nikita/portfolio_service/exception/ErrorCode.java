package korobkin.nikita.portfolio_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    PORTFOLIO_NOT_FOUND("Portfolio not found", HttpStatus.NOT_FOUND),
    PORTFOLIO_PROJECT_NOT_FOUND("Portfolio project not found", HttpStatus.NOT_FOUND);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}

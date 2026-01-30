package korobkin.nikita.user_profile_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    NICKNAME_ALREADY_EXISTS("Nickname already exists", HttpStatus.CONFLICT),
    PROFILE_NOT_FOUND("User with this id not found", HttpStatus.NOT_FOUND);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}

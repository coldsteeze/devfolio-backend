package korobkin.nikita.skill_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    SKILL_NOT_FOUND("Skill with this id not found", HttpStatus.NOT_FOUND),
    SKILL_ALREADY_EXISTS("Skill already exists", HttpStatus.CONFLICT);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}

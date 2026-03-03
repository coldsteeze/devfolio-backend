package korobkin.nikita.project_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    PROJECT_ACCESS_DENIED("You do not have permission to access this project", HttpStatus.FORBIDDEN),
    PROJECT_NOT_FOUND("Project with this id not found", HttpStatus.NOT_FOUND),
    PROJECT_SKILL_NOT_FOUND("Project skill with this id not found", HttpStatus.NOT_FOUND),
    PROJECT_SKILL_ALREADY_EXISTS("Project skill with this id already exists", HttpStatus.CONFLICT);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}


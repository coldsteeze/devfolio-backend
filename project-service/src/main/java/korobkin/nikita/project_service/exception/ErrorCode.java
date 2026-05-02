package korobkin.nikita.project_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    PROJECT_ACCESS_DENIED("You do not have permission to access this project", HttpStatus.FORBIDDEN),
    PROJECT_NOT_FOUND("Project with this id not found", HttpStatus.NOT_FOUND),
    PROJECT_SKILL_NOT_FOUND("Project skill with this id not found", HttpStatus.NOT_FOUND),
    SKILL_NOT_FOUND("Skill with this id not found", HttpStatus.NOT_FOUND),
    PROJECT_ALREADY_EXISTS("Project with this name already exists", HttpStatus.CONFLICT),
    PROJECT_SKILL_ALREADY_EXISTS("Project skill with this id already exists", HttpStatus.CONFLICT),

    MEDIA_INVALID_TYPE("Invalid image type", HttpStatus.BAD_REQUEST),
    MEDIA_FILE_TOO_LARGE("Image too large", HttpStatus.PAYLOAD_TOO_LARGE),
    MEDIA_UPLOAD_FAILED("Failed to upload image", HttpStatus.BAD_GATEWAY);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}


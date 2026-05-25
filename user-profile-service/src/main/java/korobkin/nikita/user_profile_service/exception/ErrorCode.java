package korobkin.nikita.user_profile_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    NICKNAME_ALREADY_EXISTS("Nickname already exists", HttpStatus.CONFLICT),
    PROFILE_NOT_FOUND("User with this id not found", HttpStatus.NOT_FOUND),
    PROFILE_AVATAR_NOT_FOUND("This user profile does not have avatar", HttpStatus.NOT_FOUND),

    MEDIA_INVALID_TYPE("Invalid image type", HttpStatus.BAD_REQUEST),
    MEDIA_UPLOAD_FAILED("Failed to upload image", HttpStatus.BAD_GATEWAY),
    MEDIA_INVALID_URL("Invalid file URL", HttpStatus.BAD_REQUEST),
    MEDIA_FILE_TOO_LARGE("Image too large", HttpStatus.PAYLOAD_TOO_LARGE);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}

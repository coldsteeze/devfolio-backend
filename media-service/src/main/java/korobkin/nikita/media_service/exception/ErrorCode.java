package korobkin.nikita.media_service.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    FILE_EMPTY("File is empty", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("Unsupported file type", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE("File size exceeds limit", HttpStatus.PAYLOAD_TOO_LARGE),
    INVALID_FOLDER("Invalid folder", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_ERROR("Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_URL("Invalid file URL", HttpStatus.BAD_REQUEST),
    FILE_DELETE_ERROR("Failed to delete file", HttpStatus.INTERNAL_SERVER_ERROR);

    public final String message;
    public final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}

package korobkin.nikita.media_service.exception;

public class InvalidFileTypeException extends AppException {
    public InvalidFileTypeException(ErrorCode errorCode) {
        super(errorCode);
    }
}

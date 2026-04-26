package korobkin.nikita.media_service.exception;

public class InvalidFileUrlException extends AppException {
    public InvalidFileUrlException(ErrorCode errorCode) {
        super(errorCode);
    }
}

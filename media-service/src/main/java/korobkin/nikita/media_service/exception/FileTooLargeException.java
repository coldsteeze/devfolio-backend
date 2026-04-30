package korobkin.nikita.media_service.exception;

public class FileTooLargeException extends AppException {
    public FileTooLargeException(ErrorCode errorCode) {
        super(errorCode);
    }
}

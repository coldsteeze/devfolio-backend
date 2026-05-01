package korobkin.nikita.media_service.exception;

public class FileEmptyException extends AppException {
    public FileEmptyException(ErrorCode errorCode) {
        super(errorCode);
    }
}

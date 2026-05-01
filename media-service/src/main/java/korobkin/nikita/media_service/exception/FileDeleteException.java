package korobkin.nikita.media_service.exception;

public class FileDeleteException extends AppException {
    public FileDeleteException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package korobkin.nikita.media_service.exception;

public class InvalidFolderException extends AppException {
    public InvalidFolderException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package korobkin.nikita.media_service.exception;

public class FileUploadException extends AppException {
    public FileUploadException(ErrorCode errorCode) {
        super(errorCode);
    }
}

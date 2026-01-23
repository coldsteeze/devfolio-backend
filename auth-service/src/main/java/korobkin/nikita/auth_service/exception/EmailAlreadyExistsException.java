package korobkin.nikita.auth_service.exception;

public class EmailAlreadyExistsException extends AppException {
    public EmailAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

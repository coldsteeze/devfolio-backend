package korobkin.nikita.auth_service.exception;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

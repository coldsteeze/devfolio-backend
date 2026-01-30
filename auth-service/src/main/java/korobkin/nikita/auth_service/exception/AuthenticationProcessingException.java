package korobkin.nikita.auth_service.exception;

public class AuthenticationProcessingException extends AppException {
    public AuthenticationProcessingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}

package korobkin.nikita.auth_service.exception;

public class InvalidRefreshTokenException extends AppException {
    public InvalidRefreshTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}

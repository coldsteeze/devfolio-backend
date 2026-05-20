package korobkin.nikita.project_service.exception;

public class SelfFavoriteNotAllowedException extends AppException {
    public SelfFavoriteNotAllowedException(ErrorCode errorCode) {
        super(errorCode);
    }
}

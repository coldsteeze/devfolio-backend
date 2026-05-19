package korobkin.nikita.project_service.exception;

public class ProjectAlreadyFavoritedException extends AppException {
    public ProjectAlreadyFavoritedException(ErrorCode errorCode) {
        super(errorCode);
    }
}

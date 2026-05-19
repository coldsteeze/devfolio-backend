package korobkin.nikita.project_service.exception;

public class ProjectFavoriteNotFoundException extends AppException {
    public ProjectFavoriteNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package korobkin.nikita.project_service.exception;

public class ProjectMainImageNotFoundException extends AppException {
    public ProjectMainImageNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

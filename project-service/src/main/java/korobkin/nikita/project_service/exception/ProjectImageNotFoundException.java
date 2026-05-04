package korobkin.nikita.project_service.exception;

public class ProjectImageNotFoundException extends AppException {
    public ProjectImageNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

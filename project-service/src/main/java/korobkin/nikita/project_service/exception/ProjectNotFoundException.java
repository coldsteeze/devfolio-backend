package korobkin.nikita.project_service.exception;

public class ProjectNotFoundException extends AppException {
    public ProjectNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

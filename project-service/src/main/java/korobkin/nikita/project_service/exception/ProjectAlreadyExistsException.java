package korobkin.nikita.project_service.exception;

public class ProjectAlreadyExistsException extends AppException {
    public ProjectAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

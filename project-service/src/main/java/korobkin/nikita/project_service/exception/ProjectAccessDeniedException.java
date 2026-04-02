package korobkin.nikita.project_service.exception;

public class ProjectAccessDeniedException extends AppException{
    public ProjectAccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package korobkin.nikita.project_service.exception;

public class ProjectSkillAlreadyExistsException extends AppException {
    public ProjectSkillAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package korobkin.nikita.project_service.exception;

public class ProjectSkillNotFoundException extends AppException {
    public ProjectSkillNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
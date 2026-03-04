package korobkin.nikita.project_service.exception;

public class SkillNotFoundException extends AppException {
    public SkillNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package korobkin.nikita.skill_service.exception;

public class SkillAlreadyExistsException extends AppException {
    public SkillAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}

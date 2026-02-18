package korobkin.nikita.skill_service.exception;

public class SkillNotFoundException extends AppException {
    public SkillNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

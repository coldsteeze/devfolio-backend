package korobkin.nikita.user_profile_service.exception;

public class NicknameAlreadyTakenException extends AppException {
    public NicknameAlreadyTakenException(ErrorCode errorCode) {
        super(errorCode);
    }
}

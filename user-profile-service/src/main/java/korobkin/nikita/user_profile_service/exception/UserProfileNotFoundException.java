package korobkin.nikita.user_profile_service.exception;

public class UserProfileNotFoundException extends AppException {
    public UserProfileNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

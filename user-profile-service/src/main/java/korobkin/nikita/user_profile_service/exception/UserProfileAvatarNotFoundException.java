package korobkin.nikita.user_profile_service.exception;

public class UserProfileAvatarNotFoundException extends AppException {
    public UserProfileAvatarNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

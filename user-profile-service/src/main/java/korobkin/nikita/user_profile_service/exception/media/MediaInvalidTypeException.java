package korobkin.nikita.user_profile_service.exception.media;

import korobkin.nikita.user_profile_service.exception.AppException;
import korobkin.nikita.user_profile_service.exception.ErrorCode;

public class MediaInvalidTypeException extends AppException {
    public MediaInvalidTypeException() {
        super(ErrorCode.MEDIA_INVALID_TYPE);
    }
}

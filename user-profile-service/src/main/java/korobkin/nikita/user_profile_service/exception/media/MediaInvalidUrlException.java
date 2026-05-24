package korobkin.nikita.user_profile_service.exception.media;

import korobkin.nikita.user_profile_service.exception.AppException;
import korobkin.nikita.user_profile_service.exception.ErrorCode;

public class MediaInvalidUrlException extends AppException {
    public MediaInvalidUrlException() {
        super(ErrorCode.MEDIA_INVALID_URL);
    }
}

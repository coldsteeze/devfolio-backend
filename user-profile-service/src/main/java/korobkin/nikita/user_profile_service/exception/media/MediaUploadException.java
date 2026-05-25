package korobkin.nikita.user_profile_service.exception.media;

import korobkin.nikita.user_profile_service.exception.AppException;
import korobkin.nikita.user_profile_service.exception.ErrorCode;

public class MediaUploadException extends AppException {
    public MediaUploadException() {
        super(ErrorCode.MEDIA_UPLOAD_FAILED);
    }
}

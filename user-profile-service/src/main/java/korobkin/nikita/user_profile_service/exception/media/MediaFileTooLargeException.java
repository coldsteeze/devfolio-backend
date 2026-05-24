package korobkin.nikita.user_profile_service.exception.media;

import korobkin.nikita.user_profile_service.exception.AppException;
import korobkin.nikita.user_profile_service.exception.ErrorCode;

public class MediaFileTooLargeException extends AppException {
    public MediaFileTooLargeException() {
        super(ErrorCode.MEDIA_FILE_TOO_LARGE);
    }
}

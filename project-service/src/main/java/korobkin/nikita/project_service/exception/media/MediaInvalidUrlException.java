package korobkin.nikita.project_service.exception.media;

import korobkin.nikita.project_service.exception.AppException;
import korobkin.nikita.project_service.exception.ErrorCode;

public class MediaInvalidUrlException extends AppException {
    public MediaInvalidUrlException() {
        super(ErrorCode.MEDIA_INVALID_URL);
    }
}

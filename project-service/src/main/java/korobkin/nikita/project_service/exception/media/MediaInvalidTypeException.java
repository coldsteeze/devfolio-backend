package korobkin.nikita.project_service.exception.media;

import korobkin.nikita.project_service.exception.AppException;
import korobkin.nikita.project_service.exception.ErrorCode;

public class MediaInvalidTypeException extends AppException {
    public MediaInvalidTypeException() {
        super(ErrorCode.MEDIA_INVALID_TYPE);
    }
}

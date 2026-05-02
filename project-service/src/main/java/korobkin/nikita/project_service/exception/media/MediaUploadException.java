package korobkin.nikita.project_service.exception.media;

import korobkin.nikita.project_service.exception.AppException;
import korobkin.nikita.project_service.exception.ErrorCode;

public class MediaUploadException extends AppException {
    public MediaUploadException() {
        super(ErrorCode.MEDIA_UPLOAD_FAILED);
    }
}

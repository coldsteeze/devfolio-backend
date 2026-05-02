package korobkin.nikita.project_service.exception.media;

import korobkin.nikita.project_service.exception.AppException;
import korobkin.nikita.project_service.exception.ErrorCode;

public class MediaFileTooLargeException extends AppException {
    public MediaFileTooLargeException() {
        super(ErrorCode.MEDIA_FILE_TOO_LARGE);
    }
}

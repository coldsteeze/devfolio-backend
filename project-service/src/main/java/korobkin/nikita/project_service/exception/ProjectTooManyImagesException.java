package korobkin.nikita.project_service.exception;

public class ProjectTooManyImagesException extends AppException {
    public ProjectTooManyImagesException(ErrorCode errorCode) {
        super(errorCode);
    }
}

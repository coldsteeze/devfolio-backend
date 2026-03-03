package korobkin.nikita.project_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;

    protected AppException(ErrorCode errorCode) {
        super(errorCode.message);
        this.status = errorCode.status;
        this.errorCode = errorCode;
    }
}

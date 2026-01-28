package korobkin.nikita.user_profile_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;

    protected AppException(ErrorCode errorCode) {
        super(errorCode.message);
        this.status = errorCode.status;
    }
}

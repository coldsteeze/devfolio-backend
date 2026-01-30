package korobkin.nikita.user_profile_service.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private int status;
    private String error;
    private String message;
    private String code;
    private String path;
    private LocalDateTime timestamp;
}


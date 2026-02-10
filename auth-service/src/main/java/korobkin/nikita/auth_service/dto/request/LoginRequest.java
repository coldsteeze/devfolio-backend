package korobkin.nikita.auth_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User login request")
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "example@mail.ru", description = "Valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(example = "password", description = "User password (minimum 8 characters)")
    private String password;
}

package korobkin.nikita.user_profile_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Schema(description = "Update user avatar request")
public class UpdateUserProfileAvatarRequest {

    @URL(message = "Avatar URL must be a valid URL")
    @NotBlank(message = "Avatar URL is required")
    @Schema(example = "http://avatar.url", description = "Valid avatar url")
    private String avatarUrl;
}

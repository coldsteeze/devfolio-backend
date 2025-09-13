package korobkin.nikita.user_profile_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class UpdateUserProfileAvatarRequest {

    @URL(message = "Avatar URL must be a valid URL")
    @NotBlank(message = "Avatar URL is required")
    private String avatarUrl;
}

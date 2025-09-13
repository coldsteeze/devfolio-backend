package korobkin.nikita.user_profile_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class UpdateUserProfileRequest {

    @NotBlank(message = "Nickname is required")
    @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
    private String nickname;

    @Size(max = 50, message = "Firstname must be at most 50 characters")
    private String firstName;

    @Size(max = 50, message = "Lastname must be at most 50 characters")
    private String lastName;

    @Size(max = 1000, message = "Bio must be at most 1000 characters")
    private String bio;

    @URL(message = "Avatar URL must be a valid URL")
    private String avatarUrl;

    private Set<String> skills;

    private Map<String, String> links;
}

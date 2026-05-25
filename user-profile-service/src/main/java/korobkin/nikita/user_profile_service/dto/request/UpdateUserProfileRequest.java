package korobkin.nikita.user_profile_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import korobkin.nikita.user_profile_service.entity.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Schema(description = "Update user profile request")
public class UpdateUserProfileRequest {

    @NotBlank(message = "Nickname is required")
    @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
    @Schema(example = "Nick", description = "Valid nickname")
    private String nickname;

    @Size(max = 50, message = "Firstname must be at most 50 characters")
    @Schema(example = "Nikita", description = "Valid first name")
    private String firstName;

    @Size(max = 50, message = "Lastname must be at most 50 characters")
    @Schema(example = "Korobkin", description = "Valid last name")
    private String lastName;

    @Size(max = 1000, message = "Bio must be at most 1000 characters")
    @Schema(example = "My bio", description = "Valid bio")
    private String bio;

    @Schema(example = "{\"github\": \"https://github.com/nick\"}", description = "Social links")
    private Map<String, String> links;

    @Schema(description = "User role in the platform", example = "JOB_SEEKER")
    private UserType userType;
}

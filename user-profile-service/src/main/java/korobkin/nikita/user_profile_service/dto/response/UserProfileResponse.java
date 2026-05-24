package korobkin.nikita.user_profile_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.UUID;

@Schema(description = "User profile response")
public record UserProfileResponse(

        @Schema(example = "123e4567-e89b-12d3-a456-426614174000", description = "Unique user identifier")
        UUID userId,

        @Schema(example = "Nick", description = "Valid nickname")
        String nickname,

        @Schema(example = "Nikita", description = "Valid first name")
        String firstName,

        @Schema(example = "Korobkin", description = "Valid last name")
        String lastName,

        @Schema(example = "My bio", description = "Valid bio")
        String bio,

        @Schema(example = "http://avatar.url", description = "Valid avatar url")
        String avatarUrl,

        @Schema(example = "{\"github\": \"https://github.com/nick\"}", description = "Social links")
        Map<String, String> links) {
}
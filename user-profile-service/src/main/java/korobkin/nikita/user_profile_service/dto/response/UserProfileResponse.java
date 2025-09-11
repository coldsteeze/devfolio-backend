package korobkin.nikita.user_profile_service.dto.response;

import lombok.Value;

import java.util.UUID;

@Value
public class UserProfileResponse {

    UUID userId;
    String nickname;
    String firstName;
    String lastName;
    String bio;
    String avatarUrl;
}

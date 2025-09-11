package korobkin.nikita.user_profile_service.dto.response;

import lombok.Value;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Value
public class UserProfileResponse {

    UUID userId;
    String nickname;
    String firstName;
    String lastName;
    String bio;
    String avatarUrl;
    Set<String> skills;
    Map<String, String> links;
}

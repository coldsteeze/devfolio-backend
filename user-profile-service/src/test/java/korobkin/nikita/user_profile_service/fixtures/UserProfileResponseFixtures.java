package korobkin.nikita.user_profile_service.fixtures;

import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserProfileResponseFixtures {

    public static final String DEFAULT_NICKNAME = "nickname";
    public static final String DEFAULT_FIRST_NAME = "firstName";
    public static final String DEFAULT_LAST_NAME = "lastName";
    public static final String DEFAULT_BIO = "bio";
    public static final String DEFAULT_AVATAR_URL = "http://avatarUrl";
    public static final String VALUE_SKILL = "skill1";
    public static final Set<String> DEFAULT_SKILLS = Set.of(VALUE_SKILL);
    public static final String VALUE_LINK = "github";
    public static final Map<String, String> DEFAULT_LINKS = Map.of("github", VALUE_LINK);

    public static UserProfileResponse getDefault(UUID userId) {
        return new UserProfileResponse(
                userId,
                DEFAULT_NICKNAME,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_BIO,
                DEFAULT_AVATAR_URL,
                DEFAULT_SKILLS,
                DEFAULT_LINKS
        );
    }
}

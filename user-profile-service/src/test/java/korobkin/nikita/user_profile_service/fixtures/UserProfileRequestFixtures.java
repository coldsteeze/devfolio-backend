package korobkin.nikita.user_profile_service.fixtures;

import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Set;

@UtilityClass
public class UserProfileRequestFixtures {

    public static final String DEFAULT_NICKNAME = "user";
    public static final String DEFAULT_FIRST_NAME = "first";
    public static final String DEFAULT_LAST_NAME = "last";
    public static final String DEFAULT_BIO = "bio";
    public static final String DEFAULT_AVATAR_URL = "http://avatar.url";
    public static final Set<String> DEFAULT_SKILLS = Set.of("skill1", "skill2");
    public static final Map<String, String> DEFAULT_LINKS = Map.of("github", "https://github.com");

    public static final String NEW_NICKNAME = "updatedNickname";
    public static final String NEW_FIRST_NAME = "newFirst";
    public static final String NEW_LAST_NAME = "newLast";
    public static final String NEW_BIO = "newBio";
    public static final String NEW_AVATAR_URL = "http://new.avatar.url";

    public static UpdateUserProfileRequest updateUserProfileRequest(
            String nickname,
            String firstName,
            String lastName,
            String bio,
            String avatarUrl,
            Set<String> skills,
            Map<String, String> links
            ) {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setNickname(nickname);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setBio(bio);
        request.setAvatarUrl(avatarUrl);
        request.setSkills(skills);
        request.setLinks(links);

        return request;
    }

    public static UpdateUserProfileRequest updateUserProfileRequest() {
        return updateUserProfileRequest(
                DEFAULT_NICKNAME,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_BIO,
                DEFAULT_AVATAR_URL,
                DEFAULT_SKILLS,
                DEFAULT_LINKS
        );
    }

    public static UpdateUserProfileRequest updateExistsUserProfileRequest() {
        return updateUserProfileRequest(
                NEW_NICKNAME,
                NEW_FIRST_NAME,
                NEW_LAST_NAME,
                NEW_BIO,
                DEFAULT_AVATAR_URL,
                DEFAULT_SKILLS,
                DEFAULT_LINKS
        );
    }

    public static UpdateUserProfileAvatarRequest updateUserProfileAvatarRequest(String newAvatarUrl) {
        UpdateUserProfileAvatarRequest request = new UpdateUserProfileAvatarRequest();
        request.setAvatarUrl(newAvatarUrl);

        return request;
    }

    public static UpdateUserProfileAvatarRequest updateUserProfileAvatarRequest() {
        return updateUserProfileAvatarRequest(NEW_AVATAR_URL);
    }
}

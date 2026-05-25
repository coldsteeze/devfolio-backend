package korobkin.nikita.user_profile_service.fixtures;

import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.entity.enums.UserType;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class UserProfileRequestFixtures {

    public static final String DEFAULT_NICKNAME = "user";
    public static final String DEFAULT_FIRST_NAME = "first";
    public static final String DEFAULT_LAST_NAME = "last";
    public static final String DEFAULT_BIO = "bio";
    public static final Map<String, String> DEFAULT_LINKS = Map.of("github", "https://github.com");

    public static UpdateUserProfileRequest updateUserProfileRequest(
            String nickname,
            String firstName,
            String lastName,
            String bio,
            Map<String, String> links,
            UserType type
            ) {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setNickname(nickname);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setBio(bio);
        request.setLinks(links);
        request.setUserType(type);

        return request;
    }

    public static UpdateUserProfileRequest updateUserProfileRequest() {
        return updateUserProfileRequest(
                DEFAULT_NICKNAME,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_BIO,
                DEFAULT_LINKS,
                UserType.JOB_SEEKER
        );
    }
}

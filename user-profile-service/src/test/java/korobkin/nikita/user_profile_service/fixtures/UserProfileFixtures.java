package korobkin.nikita.user_profile_service.fixtures;

import korobkin.nikita.user_profile_service.builders.UserProfileTestBuilder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserProfileFixtures {

    public static final String DEFAULT_NICKNAME = "user";

    public static final String FIRST_USER_NICKNAME = "user1";
    public static final String SECOND_USER_NICKNAME = "user2";
    public static final String FIRST_USER_SKILL = "skill1";
    public static final String SECOND_USER_SKILL = "skill2";

    public static UserProfileTestBuilder builder() {
        return new UserProfileTestBuilder()
                .withNickname(DEFAULT_NICKNAME);
    }
}

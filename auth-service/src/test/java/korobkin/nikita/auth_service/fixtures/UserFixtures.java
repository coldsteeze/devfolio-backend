package korobkin.nikita.auth_service.fixtures;

import korobkin.nikita.auth_service.entity.User;
import korobkin.nikita.auth_service.entity.enums.UserRole;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class UserFixtures {

    public static final String ENCODED_PASSWORD = "encoded-password";

    public static User defaultUser() {
        User user = new User();
        user.setEmail(AuthRequestFixtures.VALID_EMAIL);
        user.setPassword(AuthRequestFixtures.VALID_PASSWORD);
        user.setRole(UserRole.ROLE_USER);

        return user;
    }

    public static User savedUser() {
        User user = defaultUser();
        user.setId(UUID.randomUUID());
        user.setPassword(ENCODED_PASSWORD);

        return user;
    }
}

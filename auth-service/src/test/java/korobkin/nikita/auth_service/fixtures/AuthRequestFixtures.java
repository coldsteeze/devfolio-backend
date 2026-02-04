package korobkin.nikita.auth_service.fixtures;

import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthRequestFixtures {

    public static String validEmail() {
        return "test@mail.com";
    }

    public static String validPassword() {
        return "password123";
    }

    public static RegisterRequest registerRequest() {
        RegisterRequest r = new RegisterRequest();
        r.setEmail(validEmail());
        r.setPassword(validPassword());

        return r;
    }

    public static LoginRequest loginRequest() {
        LoginRequest r = new LoginRequest();
        r.setEmail(validEmail());
        r.setPassword(validPassword());

        return r;
    }

    public static RegisterRequest registerRequestWithInvalidEmail() {
        RegisterRequest r = new RegisterRequest();
        r.setEmail("");
        r.setPassword(validPassword());

        return r;
    }

    public static LoginRequest loginRequestWithInvalidCredentials() {
        LoginRequest r = new LoginRequest();
        r.setEmail("incorrect@mail.com");
        r.setPassword("incorrect");

        return r;
    }
}

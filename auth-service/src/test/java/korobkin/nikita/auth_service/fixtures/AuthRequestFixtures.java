package korobkin.nikita.auth_service.fixtures;

import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthRequestFixtures {

    public static final String VALID_EMAIL = "test@mail.com";
    public static final String VALID_PASSWORD = "password123";

    public static final String INVALID_EMAIL = "";
    public static final String INVALID_PASSWORD = "incorrect";

    public static RegisterRequest registerRequest(String email, String password) {
        RegisterRequest r = new RegisterRequest();
        r.setEmail(email);
        r.setPassword(password);

        return r;
    }

    public static LoginRequest loginRequest(String email, String password) {
        LoginRequest r = new LoginRequest();
        r.setEmail(email);
        r.setPassword(password);

        return r;
    }

    public static RegisterRequest registerRequest() {
        return registerRequest(VALID_EMAIL, VALID_PASSWORD);
    }

    public static LoginRequest loginRequest() {
        return loginRequest(VALID_EMAIL, VALID_PASSWORD);
    }

    public static RegisterRequest registerRequestWithEmptyEmail() {
        return registerRequest(INVALID_EMAIL, VALID_PASSWORD);
    }

    public static LoginRequest loginRequestWithInvalidCredentials() {
        return loginRequest(VALID_EMAIL, INVALID_PASSWORD);
    }
}

package korobkin.nikita.auth_service.service;

import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.events.UserDeletedEvent;

public interface AuthService {

    JwtTokens register(RegisterRequest registerRequest);

    JwtTokens login(LoginRequest loginRequest);

    JwtTokens refreshToken(String refreshToken);

    void logout(String refreshToken);

    void deleteUser(UserDeletedEvent userDeletedEvent);
}

package korobkin.nikita.auth_service.service;

import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RefreshTokenRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.dto.response.JwtResponse;
import korobkin.nikita.events.UserDeletedEvent;

public interface AuthService {

    JwtResponse register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest loginRequest);

    JwtResponse refreshToken(RefreshTokenRequest refreshToken);

    void logout(RefreshTokenRequest refreshToken);

    void deleteUser(UserDeletedEvent userDeletedEvent);
}

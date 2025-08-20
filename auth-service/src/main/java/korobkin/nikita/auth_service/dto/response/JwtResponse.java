package korobkin.nikita.auth_service.dto.response;

import lombok.Value;

@Value
public class JwtResponse {

    String accessToken;   // короткоживущий токен
    String refreshToken;  // долгоживущий токен
    long expiresIn;       // через сколько секунд accessToken истечет
}

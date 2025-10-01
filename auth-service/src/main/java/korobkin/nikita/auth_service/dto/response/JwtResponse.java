package korobkin.nikita.auth_service.dto.response;

import lombok.Value;

@Value
public class JwtResponse {

    String accessToken;
    long accessTokenExpiresIn;
}

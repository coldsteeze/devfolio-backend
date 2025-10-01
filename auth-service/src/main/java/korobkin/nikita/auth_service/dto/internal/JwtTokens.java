package korobkin.nikita.auth_service.dto.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtTokens {

    private final String accessToken;
    private final String refreshToken;
    private final long accessTokenExpiresIn;
}

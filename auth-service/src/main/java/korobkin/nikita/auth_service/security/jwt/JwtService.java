package korobkin.nikita.auth_service.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.UUID;

public interface JwtService {

    String generateAccessToken(UUID userId, String email);

    String generateRefreshToken(UUID userId, String email);

    DecodedJWT verify(String token);

    boolean isAccessToken(DecodedJWT jwt);

    boolean isRefreshToken(DecodedJWT jwt);

    String getEmailFromVerifiedToken(DecodedJWT jwt);

    UUID getUserIdFromVerifiedToken(DecodedJWT jwt);
}

package korobkin.nikita.user_profile_service.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.UUID;

public interface JwtService {

    DecodedJWT verifyAccessToken(String token) throws JWTVerificationException;

    UUID getUserIdFromVerifiedToken(DecodedJWT jwt);

    String getEmailFromVerifiedToken(DecodedJWT jwt);
}

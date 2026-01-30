package korobkin.nikita.user_profile_service.security.jwt.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import korobkin.nikita.user_profile_service.config.JwtProperties;
import korobkin.nikita.user_profile_service.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    public static final String ACCESS_TOKEN_TYPE = "access_token";

    private final JwtProperties jwtProperties;

    @Override
    public DecodedJWT verifyAccessToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtProperties.getSecret()))
                .withIssuer(jwtProperties.getIssuer())
                .build()
                .verify(token);

        if (!ACCESS_TOKEN_TYPE.equals(jwt.getClaim("type").asString())) {
            throw new JWTVerificationException("Invalid token type");
        }

        return jwt;
    }

    @Override
    public UUID getUserIdFromVerifiedToken(DecodedJWT jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @Override
    public String getEmailFromVerifiedToken(DecodedJWT jwt) {
        return jwt.getClaim("email").asString();
    }
}

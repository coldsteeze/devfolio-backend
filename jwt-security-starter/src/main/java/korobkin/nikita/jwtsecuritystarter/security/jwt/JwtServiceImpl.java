package korobkin.nikita.jwtsecuritystarter.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import korobkin.nikita.jwtsecuritystarter.config.JwtProperties;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class JwtServiceImpl implements JwtService {

    public static final String ACCESS_TOKEN_TYPE = "access_token";
    private final JwtProperties jwtProperties;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public DecodedJWT verifyAccessToken(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtProperties.getSecret()))
                .withIssuer(jwtProperties.getIssuer())
                .build()
                .verify(token);

        if (!ACCESS_TOKEN_TYPE.equals(jwt.getClaim("type").asString())) {
            throw new JWTVerificationException("Invalid token type. Expected: " + ACCESS_TOKEN_TYPE);
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

    @Override
    public List<String> getRolesFromVerifiedToken(DecodedJWT jwt) {
        String role = jwt.getClaim("role").asString();

        if (role != null && !role.trim().isEmpty()) {
            return Collections.singletonList(role.trim());
        }

        return Collections.emptyList();
    }
}

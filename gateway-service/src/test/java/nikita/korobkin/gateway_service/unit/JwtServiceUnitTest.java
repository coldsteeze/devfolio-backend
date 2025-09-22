package nikita.korobkin.gateway_service.unit;

import com.auth0.jwt.exceptions.JWTVerificationException;
import nikita.korobkin.gateway_service.config.JwtProperties;
import nikita.korobkin.gateway_service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtServiceUnitTest {

    private JwtService jwtService;
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret");
        jwtProperties.setIssuer("test-issuer");

        jwtService = new JwtService(jwtProperties);
        jwtUtils = new JwtUtils(jwtProperties);
    }

    @Test
    void validateToken_shouldPassForValidToken() {
        String validToken = jwtUtils.generateAccessToken(UUID.randomUUID(), "test@mail.com");
        assertDoesNotThrow(() -> jwtService.validateToken(validToken));
    }

    @Test
    void validateToken_shouldThrowForInvalidToken() {
        String invalidToken = "not-a-valid-token";
        assertThrows(JWTVerificationException.class, () -> jwtService.validateToken(invalidToken));
    }
}

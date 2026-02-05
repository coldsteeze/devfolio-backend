package korobkin.nikita.auth_service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import korobkin.nikita.auth_service.config.AuthCookieProperties;
import korobkin.nikita.auth_service.fixtures.AuthRequestFixtures;
import korobkin.nikita.auth_service.fixtures.JwtTokenFixtures;
import korobkin.nikita.auth_service.repository.UserRepository;
import korobkin.nikita.auth_service.security.jwt.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AuthCookieProperties authCookieProperties;

    @Autowired
    private JwtProperties jwtProperties;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }

    @Test
    void registerUser_success() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.registerRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.accessTokenExpiresIn").isNumber())
                .andExpect(validAuthCookie());
    }

    @Test
    void registerUser_duplicateEmail_fails() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.registerRequest())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.registerRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Email already exists"))
                .andExpect(jsonPath("$.code")
                        .value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void registerUser_invalidEmail_fails() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.registerRequestWithEmptyEmail())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void loginUser_success() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.registerRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.accessTokenExpiresIn").isNumber())
                .andExpect(validAuthCookie());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.loginRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.accessTokenExpiresIn").isNumber())
                .andExpect(validAuthCookie());
    }

    @Test
    void loginUser_emptyEmail_fails() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.loginRequestWithEmptyEmail())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("email Email is required"))
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));
    }

    @Test
    void loginUser_invalidCredentials_fails() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.registerRequest())));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(AuthRequestFixtures.loginRequestWithInvalidCredentials())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"))
                .andExpect(jsonPath("$.code")
                        .value("INVALID_CREDENTIALS"));
    }

    @Test
    void refreshToken_invalidToken_fails() throws Exception {
        Cookie cookie = new Cookie(authCookieProperties.getName(), JwtTokenFixtures.INVALID_TOKEN);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.code")
                        .value("REFRESH_TOKEN_INVALID"));
    }

    @Test
    void refreshToken_expiredToken_fails() throws Exception {
        Cookie cookie = new Cookie(
                authCookieProperties.getName(),
                JwtTokenFixtures.createExpiredRefreshToken(jwtProperties)
        );

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.code")
                        .value("REFRESH_TOKEN_EXPIRED"));
    }

    @Test
    void refreshToken_withoutCookies_fails() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.code")
                        .value("REFRESH_TOKEN_MISSING"));
    }

    @Test
    void logout_success() throws Exception {
        Cookie cookie = new Cookie(
                authCookieProperties.getName(),
                JwtTokenFixtures.createValidRefreshToken(jwtProperties)
        );

        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_invalidToken_fails() throws Exception {
        Cookie cookie = new Cookie(authCookieProperties.getName(), JwtTokenFixtures.INVALID_TOKEN);

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isNoContent());
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    private ResultMatcher validAuthCookie() {
        return result -> {
            Cookie cookie = result.getResponse()
                    .getCookie(authCookieProperties.getName());

            assertThat(cookie).isNotNull();
            assertThat(cookie.isHttpOnly()).isEqualTo(authCookieProperties.isHttpOnly());
            assertThat(cookie.getSecure()).isEqualTo(authCookieProperties.isSecure());
            assertThat(cookie.getPath()).isEqualTo(authCookieProperties.getPath());
            assertThat(cookie.getMaxAge()).isPositive();
        };
    }
}
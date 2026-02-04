package korobkin.nikita.auth_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import korobkin.nikita.auth_service.config.AuthCookieProperties;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.fixtures.AuthRequestFixtures;
import korobkin.nikita.auth_service.fixtures.RefreshTokenFixtures;
import korobkin.nikita.auth_service.repository.UserRepository;
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
        RegisterRequest request = AuthRequestFixtures.registerRequest();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(validAuthBody())
                .andExpect(validAuthCookie());
    }

    @Test
    void registerUser_duplicateEmail_fails() throws Exception {
        RegisterRequest request = AuthRequestFixtures.registerRequest();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Email already exists"))
                .andExpect(jsonPath("$.code")
                        .value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void registerUser_invalidEmail_fails() throws Exception {
        RegisterRequest request = AuthRequestFixtures.registerRequestWithInvalidEmail();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void loginUser_success() throws Exception {
        RegisterRequest register = AuthRequestFixtures.registerRequest();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(validAuthBody())
                .andExpect(validAuthCookie());

        LoginRequest login = AuthRequestFixtures.loginRequest();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(validAuthBody())
                .andExpect(validAuthCookie());
    }

    @Test
    void refreshToken_invalidToken_fails() throws Exception {
        String token = RefreshTokenFixtures.INVALID_TOKEN;

        Cookie cookie = new Cookie(authCookieProperties.getName(), token);

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
    void refreshToken_withoutCookies_fails() throws Exception {

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.code")
                        .value("REFRESH_TOKEN_MISSING"));
    }

    private ResultMatcher validAuthBody() {
        return result -> {
            String json = result.getResponse().getContentAsString();

            String token = JsonPath.read(json, "$.accessToken");
            Integer expiresIn = JsonPath.read(json, "$.accessTokenExpiresIn");

            assertThat(token).isNotBlank();
            assertThat(expiresIn).isPositive();
        };
    }

    private ResultMatcher validAuthCookie() {
        return result -> {
            Cookie cookie = result.getResponse()
                    .getCookie(authCookieProperties.getName());

            assertThat(cookie).isNotNull();
            assertThat(cookie.isHttpOnly()).isEqualTo(authCookieProperties.isHttpOnly());
            assertThat(cookie.getSecure()).isEqualTo(authCookieProperties.isSecure());
            assertThat(cookie.getPath()).isEqualTo(authCookieProperties.getPath());
            assertThat(cookie.getMaxAge())
                    .isEqualTo((int) authCookieProperties.getMaxAgeDays() * 24 * 3600);
        };
    }
}


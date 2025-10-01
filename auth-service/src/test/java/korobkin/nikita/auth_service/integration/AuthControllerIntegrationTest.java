package korobkin.nikita.auth_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import korobkin.nikita.auth_service.config.AuthCookieProperties;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest extends AbstractIntegrationTest{

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
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void registerUser_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.accessTokenExpiresIn").isNotEmpty())
                .andExpect(cookie().exists(authCookieProperties.getName()))
                .andExpect(cookie().httpOnly(authCookieProperties.getName(),
                        authCookieProperties.isHttpOnly()))
                .andExpect(cookie().secure(authCookieProperties.getName(),
                        authCookieProperties.isSecure()))
                .andExpect(cookie().path(authCookieProperties.getName(),
                        authCookieProperties.getPath()))
                .andExpect(cookie().maxAge(authCookieProperties.getName(),
                        (int) authCookieProperties.getMaxAgeDays() * 24 * 3600));
    }

    @Test
    void registerUser_duplicateEmail_fails() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("dup@mail.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("User with this email already exists"));
    }

    @Test
    void loginUser_success() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("login@mail.com");
        register.setPassword("password");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.accessTokenExpiresIn").isNotEmpty())
                .andExpect(cookie().exists(authCookieProperties.getName()))
                .andExpect(cookie().httpOnly(authCookieProperties.getName(),
                        authCookieProperties.isHttpOnly()))
                .andExpect(cookie().secure(authCookieProperties.getName(),
                        authCookieProperties.isSecure()))
                .andExpect(cookie().path(authCookieProperties.getName(),
                        authCookieProperties.getPath()))
                .andExpect(cookie().maxAge(authCookieProperties.getName(),
                        (int) authCookieProperties.getMaxAgeDays() * 24 * 3600));

        LoginRequest login = new LoginRequest();
        login.setEmail("login@mail.com");
        login.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.accessTokenExpiresIn").isNotEmpty())
                .andExpect(cookie().exists(authCookieProperties.getName()))
                .andExpect(cookie().httpOnly(authCookieProperties.getName(),
                        authCookieProperties.isHttpOnly()))
                .andExpect(cookie().secure(authCookieProperties.getName(),
                        authCookieProperties.isSecure()))
                .andExpect(cookie().path(authCookieProperties.getName(),
                        authCookieProperties.getPath()))
                .andExpect(cookie().maxAge(authCookieProperties.getName(),
                        (int) authCookieProperties.getMaxAgeDays() * 24 * 3600));
    }

    @Test
    void refreshToken_invalidToken_fails() throws Exception {
        String token = "aaa.bbb.ccc";

        Cookie cookie = new Cookie(authCookieProperties.getName(), token);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Invalid or expired refresh token"));
    }

    @Test
    void registerUser_invalidEmail_fails() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("");
        request.setPassword("pass123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}


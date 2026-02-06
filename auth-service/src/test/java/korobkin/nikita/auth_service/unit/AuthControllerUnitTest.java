package korobkin.nikita.auth_service.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import korobkin.nikita.auth_service.controller.AuthController;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.exception.EmailAlreadyExistsException;
import korobkin.nikita.auth_service.exception.ErrorCode;
import korobkin.nikita.auth_service.exception.InvalidCredentialsException;
import korobkin.nikita.auth_service.exception.InvalidRefreshTokenException;
import korobkin.nikita.auth_service.fixtures.AuthRequestFixtures;
import korobkin.nikita.auth_service.fixtures.JwtTokenFixtures;
import korobkin.nikita.auth_service.security.config.SecurityConfig;
import korobkin.nikita.auth_service.security.jwt.impl.JwtServiceImpl;
import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.auth_service.service.CookieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@DisplayName("AuthController unit tests")
class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieService cookieService;

    @Nested
    @DisplayName("POST /api/auth/register - User registration")
    class RegisterEndpoint {

        @Test
        @DisplayName("Should register user successfully")
        void registerUser_success() throws Exception {
            given(authService.register(any(RegisterRequest.class)))
                    .willReturn(JwtTokenFixtures.jwtTokens());

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(AuthRequestFixtures.registerRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken")
                            .value(JwtTokenFixtures.DEFAULT_ACCESS_TOKEN))
                    .andExpect(jsonPath("$.accessTokenExpiresIn")
                            .value(JwtTokenFixtures.DEFAULT_EXPIRES_IN));

            verify(authService).register(any(RegisterRequest.class));
            verify(cookieService).addRefreshTokenToCookie(any(HttpServletResponse.class), eq(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN));
        }

        @Test
        @DisplayName("Should return 400 when email is empty")
        void registerUser_invalidEmail_fails() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(AuthRequestFixtures.registerRequestWithEmptyEmail())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void registerUser_duplicateEmail_fails() throws Exception {
            given(authService.register(any(RegisterRequest.class)))
                    .willThrow(new EmailAlreadyExistsException(ErrorCode.EMAIL_ALREADY_EXISTS));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(AuthRequestFixtures.registerRequest())))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login - User authentication")
    class LoginEndpoint {

        @Test
        @DisplayName("Should authenticate user successfully")
        void loginUser_success() throws Exception {
            given(authService.login(any(LoginRequest.class)))
                    .willReturn(JwtTokenFixtures.jwtTokens());

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(AuthRequestFixtures.loginRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken")
                            .value(JwtTokenFixtures.DEFAULT_ACCESS_TOKEN))
                    .andExpect(jsonPath("$.accessTokenExpiresIn")
                            .value(JwtTokenFixtures.DEFAULT_EXPIRES_IN));

            verify(authService).login(any(LoginRequest.class));
            verify(cookieService).addRefreshTokenToCookie(any(HttpServletResponse.class), eq(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN));
        }

        @Test
        @DisplayName("Should return 400 when email is empty")
        void loginUser_invalidEmail_fails() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(AuthRequestFixtures.loginRequestWithEmptyEmail())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Should return 401 when credentials are invalid")
        void loginUser_invalidCredentials_fails() throws Exception {
            given(authService.login(any(LoginRequest.class)))
                    .willThrow(new InvalidCredentialsException(ErrorCode.INVALID_CREDENTIALS));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(AuthRequestFixtures.loginRequestWithInvalidCredentials())))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/refresh - Token refresh")
    class RefreshEndpoint {

        @Test
        @DisplayName("Should refresh tokens successfully")
        void refreshToken_success() throws Exception {
            given(cookieService.extractRefreshTokenFromCookie(any(HttpServletRequest.class)))
                    .willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            given(authService.refreshToken(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN))
                    .willReturn(JwtTokenFixtures.newJwtTokens());

            mockMvc.perform(post("/api/auth/refresh"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken")
                            .value(JwtTokenFixtures.NEW_ACCESS_TOKEN))
                    .andExpect(jsonPath("$.accessTokenExpiresIn")
                            .value(JwtTokenFixtures.DEFAULT_EXPIRES_IN));

            verify(cookieService).extractRefreshTokenFromCookie(any(HttpServletRequest.class));
            verify(authService).refreshToken(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            verify(cookieService).addRefreshTokenToCookie(any(HttpServletResponse.class), eq(JwtTokenFixtures.NEW_REFRESH_TOKEN));
        }

        @Test
        @DisplayName("Should return 401 when refresh token is invalid")
        void refreshToken_invalidToken_fails() throws Exception {
            given(cookieService.extractRefreshTokenFromCookie(any(HttpServletRequest.class)))
                    .willReturn(JwtTokenFixtures.INVALID_TOKEN);
            given(authService.refreshToken(JwtTokenFixtures.INVALID_TOKEN))
                    .willThrow(new InvalidRefreshTokenException(ErrorCode.REFRESH_TOKEN_INVALID));

            mockMvc.perform(post("/api/auth/refresh"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_INVALID"));
        }

        @Test
        @DisplayName("Should return 401 when refresh token is expired")
        void refreshToken_expiredToken_fails() throws Exception {
            given(cookieService.extractRefreshTokenFromCookie(any(HttpServletRequest.class)))
                    .willReturn(JwtTokenFixtures.INVALID_TOKEN);
            given(authService.refreshToken(JwtTokenFixtures.INVALID_TOKEN))
                    .willThrow(new InvalidRefreshTokenException(ErrorCode.REFRESH_TOKEN_EXPIRED));

            mockMvc.perform(post("/api/auth/refresh"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_EXPIRED"));
        }

        @Test
        @DisplayName("Should return 401 when refresh token cookie is missing")
        void refreshToken_withoutCookie_fails() throws Exception {
            given(cookieService.extractRefreshTokenFromCookie(any(HttpServletRequest.class)))
                    .willThrow(new InvalidRefreshTokenException(ErrorCode.REFRESH_TOKEN_MISSING));

            mockMvc.perform(post("/api/auth/refresh"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_MISSING"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout - User logout")
    class LogoutEndpoint {

        @Test
        @DisplayName("Should logout successfully")
        void logout_success() throws Exception {
            given(cookieService.extractRefreshTokenFromCookie(any(HttpServletRequest.class)))
                    .willReturn(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);

            mockMvc.perform(post("/api/auth/logout"))
                    .andExpect(status().isNoContent());

            verify(authService).logout(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            verify(cookieService).clearRefreshTokenCookie(any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("Should return 401 when refresh token cookie is missing")
        void logout_withoutCookies_fails() throws Exception {
            given(cookieService.extractRefreshTokenFromCookie(any(HttpServletRequest.class)))
                    .willThrow(new InvalidRefreshTokenException(ErrorCode.REFRESH_TOKEN_MISSING));

            mockMvc.perform(post("/api/auth/logout"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_MISSING"));
        }
    }

    private String json(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }
}

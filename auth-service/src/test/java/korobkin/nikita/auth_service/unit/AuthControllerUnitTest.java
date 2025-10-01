package korobkin.nikita.auth_service.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import korobkin.nikita.auth_service.controller.AuthController;
import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.security.JwtService;
import korobkin.nikita.auth_service.security.SecurityConfig;
import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.auth_service.service.CookieService;
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
class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieService cookieService;

    @Test
    void registerUser_success() throws Exception {
        given(authService.register(any(RegisterRequest.class)))
                .willReturn(new JwtTokens("access", "refresh", 15));

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.accessTokenExpiresIn").value(15));

        verify(authService).register(any(RegisterRequest.class));
        verify(cookieService).addRefreshTokenToCookie(any(HttpServletResponse.class), eq("refresh"));
    }

    @Test
    void registerUser_invalidEmail_fails() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void loginUser_success() throws Exception {
        given(authService.login(any(LoginRequest.class)))
                .willReturn(new JwtTokens("access", "refresh", 15));

        LoginRequest request = new LoginRequest();
        request.setEmail("user@mail.com");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.accessTokenExpiresIn").value(15));

        verify(authService).login(any(LoginRequest.class));
        verify(cookieService).addRefreshTokenToCookie(any(HttpServletResponse.class), eq("refresh"));
    }

    @Test
    void refresh_success() throws Exception {
        given(cookieService.extractRefreshTokenFromCookie(any(HttpServletRequest.class)))
                .willReturn("refresh-token");
        given(authService.refreshToken("refresh-token"))
                .willReturn(new JwtTokens("new-access", "new-refresh", 15));

        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.accessTokenExpiresIn").value(15));

        verify(cookieService).extractRefreshTokenFromCookie(any(HttpServletRequest.class));
        verify(authService).refreshToken("refresh-token");
        verify(cookieService).addRefreshTokenToCookie(any(HttpServletResponse.class), eq("new-refresh"));
    }

    @Test
    void logout_success() throws Exception {
        given(cookieService.extractRefreshTokenFromCookie(any(HttpServletRequest.class)))
                .willReturn("refresh-token");

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());

        verify(authService).logout("refresh-token");
        verify(cookieService).clearRefreshTokenCookie(any(HttpServletResponse.class));
    }
}

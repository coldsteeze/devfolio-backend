package korobkin.nikita.auth_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import korobkin.nikita.auth_service.docs.AuthControllerDocs;
import korobkin.nikita.auth_service.dto.internal.JwtTokens;
import korobkin.nikita.auth_service.dto.request.LoginRequest;
import korobkin.nikita.auth_service.dto.request.RegisterRequest;
import korobkin.nikita.auth_service.dto.response.JwtResponse;
import korobkin.nikita.auth_service.service.AuthService;
import korobkin.nikita.auth_service.service.CookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest registerRequest,
                                                HttpServletResponse response) {
        JwtTokens tokens = authService.register(registerRequest);
        cookieService.addRefreshTokenToCookie(response, tokens.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtResponse(tokens.getAccessToken(), tokens.getAccessTokenExpiresIn()));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                             HttpServletResponse response) {
        JwtTokens tokens = authService.login(loginRequest);
        cookieService.addRefreshTokenToCookie(response, tokens.getRefreshToken());
        return ResponseEntity.ok(new JwtResponse(tokens.getAccessToken(), tokens.getAccessTokenExpiresIn()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(HttpServletResponse response,
                                               HttpServletRequest request) {
        String refreshToken = cookieService.extractRefreshTokenFromCookie(request);
        JwtTokens tokens = authService.refreshToken(refreshToken);
        cookieService.addRefreshTokenToCookie(response, tokens.getRefreshToken());
        return ResponseEntity.ok(new JwtResponse(tokens.getAccessToken(), tokens.getAccessTokenExpiresIn()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.extractRefreshTokenFromCookie(request);
        authService.logout(refreshToken);
        cookieService.clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }
}

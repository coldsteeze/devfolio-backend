package korobkin.nikita.auth_service.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {

    void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken);

    String extractRefreshTokenFromCookie(HttpServletRequest request);

    void clearRefreshTokenCookie(HttpServletResponse response);
}

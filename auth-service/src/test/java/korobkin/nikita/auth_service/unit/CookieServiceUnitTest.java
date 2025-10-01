package korobkin.nikita.auth_service.unit;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import korobkin.nikita.auth_service.config.AuthCookieProperties;
import korobkin.nikita.auth_service.service.impl.CookieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class CookieServiceUnitTest {

    private CookieServiceImpl cookieService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        AuthCookieProperties properties = new AuthCookieProperties();
        properties.setName("refreshToken");
        properties.setHttpOnly(true);
        properties.setSecure(true);
        properties.setSameSite("None");
        properties.setPath("/api/auth");
        properties.setMaxAgeDays(7);

        cookieService = new CookieServiceImpl(properties);
    }

    private String captureCookieHeader() {
        ArgumentCaptor<String> headerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpServletResponse).addHeader(headerNameCaptor.capture(), headerValueCaptor.capture());
        assertThat(headerNameCaptor.getValue()).isEqualTo("Set-Cookie");
        return headerValueCaptor.getValue();
    }

    @Test
    void addRefreshTokenToCookie_shouldAddCookieWithCorrectProperties() {
        String refreshToken = "refreshToken";

        cookieService.addRefreshTokenToCookie(httpServletResponse, refreshToken);
        String cookieHeader = captureCookieHeader();

        assertThat(cookieHeader).contains("refreshToken=" + refreshToken);
        assertThat(cookieHeader).contains("HttpOnly");
        assertThat(cookieHeader).contains("Secure");
        assertThat(cookieHeader).contains("SameSite=None");
        assertThat(cookieHeader).contains("Path=/api/auth");
        assertThat(cookieHeader).contains("Max-Age=604800");
    }

    @Test
    void extractRefreshTokenFromCookie_shouldReturnToken_whenCookieExists() {
        Cookie realCookie = new Cookie("refreshToken", "test-token-value");
        given(httpServletRequest.getCookies()).willReturn(new Cookie[]{realCookie});

        String refreshToken = cookieService.extractRefreshTokenFromCookie(httpServletRequest);

        assertThat(refreshToken).isEqualTo("test-token-value");
    }

    @Test
    void extractRefreshTokenFromCookie_shouldThrowException_whenCookiesAreNull() {
        given(httpServletRequest.getCookies()).willReturn(null);

        assertThatThrownBy(() -> cookieService.extractRefreshTokenFromCookie(httpServletRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token cookie not found");
    }

    @Test
    void extractRefreshTokenFromCookie_shouldThrowException_whenSpecificCookieNotFound() {
        Cookie realCookie = new Cookie("wrongCookie", "value");
        given(httpServletRequest.getCookies()).willReturn(new Cookie[]{realCookie});

        assertThatThrownBy(() -> cookieService.extractRefreshTokenFromCookie(httpServletRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token cookie not found");
    }

    @Test
    void extractRefreshTokenFromCookie_shouldReturnToken_whenMultipleCookiesExist() {
        Cookie correctCookie = new Cookie("refreshToken", "token1");
        Cookie otherCookie = new Cookie("refreshToken1", "token2");
        given(httpServletRequest.getCookies()).willReturn(new Cookie[]{correctCookie, otherCookie});

        String refreshToken = cookieService.extractRefreshTokenFromCookie(httpServletRequest);

        assertThat(refreshToken).isEqualTo("token1");
    }

    @Test
    void clearRefreshTokenCookie_shouldAddExpiredCookie() {
        cookieService.clearRefreshTokenCookie(httpServletResponse);

        String cookieHeader = captureCookieHeader();
        assertThat(cookieHeader).contains("Max-Age=0");
        assertThat(cookieHeader).contains("refreshToken=");
    }

    @Test
    void clearRefreshTokenCookie_shouldUseSamePropertiesAsAddMethod() {
        cookieService.clearRefreshTokenCookie(httpServletResponse);

        String cookieHeader = captureCookieHeader();
        assertThat(cookieHeader).contains("refreshToken=");
        assertThat(cookieHeader).contains("HttpOnly");
        assertThat(cookieHeader).contains("Secure");
        assertThat(cookieHeader).contains("SameSite=None");
        assertThat(cookieHeader).contains("Path=/api/auth");
        assertThat(cookieHeader).contains("Max-Age=0");
    }
}



package korobkin.nikita.auth_service.unit;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import korobkin.nikita.auth_service.config.AuthCookieProperties;
import korobkin.nikita.auth_service.exception.InvalidRefreshTokenException;
import korobkin.nikita.auth_service.fixtures.CookieFixtures;
import korobkin.nikita.auth_service.fixtures.JwtTokenFixtures;
import korobkin.nikita.auth_service.service.impl.CookieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("CookieService unit tests")
public class CookieServiceUnitTest {

    private CookieServiceImpl cookieService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        AuthCookieProperties properties = CookieFixtures.authCookieProperties();

        cookieService = new CookieServiceImpl(properties);
    }

    @Nested
    @DisplayName("Add refresh token to cookie")
    class AddRefreshToken {

        @Test
        @DisplayName("Should add cookie with correct properties")
        void addRefreshTokenToCookie_shouldAddCookieWithCorrectProperties() {
            cookieService.addRefreshTokenToCookie(httpServletResponse, JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            String cookieHeader = captureCookieHeader();

            assertThat(cookieHeader).contains("refreshToken=" + JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            assertThat(cookieHeader).contains("HttpOnly");
            assertThat(cookieHeader).contains("Secure");
            assertThat(cookieHeader).contains("SameSite=None");
            assertThat(cookieHeader).contains("Path=/api/auth");
            assertThat(cookieHeader).contains("Max-Age=604800");
        }
    }

    @Nested
    @DisplayName("Extract refresh token from cookie")
    class ExtractRefreshToken {

        @Test
        @DisplayName("Should return token when cookie exists")
        void extractRefreshTokenFromCookie_shouldReturnToken_whenCookieExists() {
            Cookie realCookie = new Cookie(CookieFixtures.VALID_NAME, JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            given(httpServletRequest.getCookies()).willReturn(new Cookie[]{realCookie});

            String refreshToken = cookieService.extractRefreshTokenFromCookie(httpServletRequest);

            assertThat(refreshToken).isEqualTo(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("Should throw exception when cookies are null")
        void extractRefreshTokenFromCookie_shouldThrowException_whenCookiesAreNull() {
            given(httpServletRequest.getCookies()).willReturn(null);

            assertThatThrownBy(() -> cookieService.extractRefreshTokenFromCookie(httpServletRequest))
                    .isInstanceOf(InvalidRefreshTokenException.class)
                    .hasMessageContaining("Unauthorized");
        }

        @Test
        @DisplayName("Should throw exception when cookie not found")
        void extractRefreshTokenFromCookie_shouldThrowException_whenSpecificCookieNotFound() {
            Cookie realCookie = new Cookie(CookieFixtures.INVALID_NAME, JwtTokenFixtures.INVALID_TOKEN);
            given(httpServletRequest.getCookies()).willReturn(new Cookie[]{realCookie});

            assertThatThrownBy(() -> cookieService.extractRefreshTokenFromCookie(httpServletRequest))
                    .isInstanceOf(InvalidRefreshTokenException.class)
                    .hasMessageContaining("Unauthorized");
        }

        @Test
        @DisplayName("Should return token when multiple cookie exist")
        void extractRefreshTokenFromCookie_shouldReturnToken_whenMultipleCookiesExist() {
            Cookie correctCookie = CookieFixtures.cookie(CookieFixtures.VALID_NAME, JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
            Cookie otherCookie = CookieFixtures.cookie(CookieFixtures.INVALID_NAME, JwtTokenFixtures.INVALID_TOKEN);

            given(httpServletRequest.getCookies()).willReturn(new Cookie[]{correctCookie, otherCookie});

            String refreshToken = cookieService.extractRefreshTokenFromCookie(httpServletRequest);

            assertThat(refreshToken).isEqualTo(JwtTokenFixtures.DEFAULT_REFRESH_TOKEN);
        }
    }

    @Nested
    @DisplayName("Clear refresh token from cookie")
    class ClearRefreshToken {

        @Test
        @DisplayName("Should add expired cookie")
        void clearRefreshTokenCookie_shouldAddExpiredCookie() {
            cookieService.clearRefreshTokenCookie(httpServletResponse);

            String cookieHeader = captureCookieHeader();
            assertThat(cookieHeader).contains("Max-Age=0");
            assertThat(cookieHeader).contains("refreshToken=");
        }

        @Test
        @DisplayName("Should use same properties as add method")
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

    private String captureCookieHeader() {
        ArgumentCaptor<String> headerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpServletResponse).addHeader(headerNameCaptor.capture(), headerValueCaptor.capture());
        assertThat(headerNameCaptor.getValue()).isEqualTo("Set-Cookie");
        return headerValueCaptor.getValue();
    }
}



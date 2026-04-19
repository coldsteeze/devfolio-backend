package nikita.korobkin.gateway_service.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import nikita.korobkin.gateway_service.security.JwtService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GatewayIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private JwtService jwtService;

    private static WireMockServer authWireMock;
    private static WireMockServer profileWireMock;
    private static WireMockServer skillWireMock;
    private static WireMockServer projectWireMock;
    private static WireMockServer portfolioWireMock;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        authWireMock = new WireMockServer(options().dynamicPort());
        profileWireMock = new WireMockServer(options().dynamicPort());
        skillWireMock = new WireMockServer(options().dynamicPort());
        projectWireMock = new WireMockServer(options().dynamicPort());
        portfolioWireMock = new WireMockServer(options().dynamicPort());

        authWireMock.start();
        profileWireMock.start();
        skillWireMock.start();
        projectWireMock.start();
        portfolioWireMock.start();

        authWireMock.stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"accessToken\":\"mocked-jwt-token\"}")));

        profileWireMock.stubFor(get(urlEqualTo("/api/profiles/me"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"id\":\"user-123\",\"nickname\":\"mocked\"}")));

        registry.add("services.auth-service-uri", () -> "http://localhost:" + authWireMock.port());
        registry.add("services.user-profile-service-uri", () -> "http://localhost:" + profileWireMock.port());
        registry.add("services.skill-service-uri", () -> "http://localhost:" + skillWireMock.port());
        registry.add("services.project-service-uri", () -> "http://localhost:" + projectWireMock.port());
        registry.add("services.portfolio-service-uri", () -> "http://localhost:" + portfolioWireMock.port());
    }

    @AfterAll
    static void stopWireMocks() {
        if (authWireMock != null) authWireMock.stop();
        if (profileWireMock != null) profileWireMock.stop();
    }

    @BeforeEach
    void setup() {
        doNothing().when(jwtService).validateToken(anyString());
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void publicEndpoint_shouldReturnToken() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl() + "/api/auth/login", null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("mocked-jwt-token");
    }

    @Test
    void protectedEndpoint_shouldReturnProfile() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer mocked-jwt-token");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/api/profiles/me",
                HttpMethod.GET,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("mocked");
    }
}

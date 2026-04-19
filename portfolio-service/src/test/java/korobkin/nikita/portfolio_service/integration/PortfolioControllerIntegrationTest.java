package korobkin.nikita.portfolio_service.integration;

import korobkin.nikita.portfolio_service.exception.ErrorCode;
import korobkin.nikita.portfolio_service.fixtures.PortfolioFixtures;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.security.user.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class PortfolioControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    void shouldReturnPortfolioByUserId() throws Exception {
        UUID userId = UUID.randomUUID();

        portfolioRepository.save(
                PortfolioFixtures.valid(userId)
        );

        mockMvc.perform(get("/api/portfolios/{userId}", userId)
                .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getPortfolio_withInvalidUserId_shouldReturnNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/api/portfolios/{userId}", userId)
                        .with(auth(UUID.randomUUID())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorCode.PORTFOLIO_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.PORTFOLIO_NOT_FOUND.name()));
    }

    @Test
    void shouldReturnMyPortfolio() throws Exception {
        UUID userId = UUID.randomUUID();

        portfolioRepository.save(
                PortfolioFixtures.valid(userId)
        );

        mockMvc.perform(get("/api/portfolios/me")
                .with(auth(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    private RequestPostProcessor auth(UUID userId) {
        return authentication(new UsernamePasswordAuthenticationToken(
                new UserPrincipal(userId),
                null,
                Collections.emptyList()
        ));
    }
}

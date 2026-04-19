package korobkin.nikita.portfolio_service.integration;

import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.exception.PortfolioNotFoundException;
import korobkin.nikita.portfolio_service.fixtures.PortfolioEventFixtures;
import korobkin.nikita.portfolio_service.fixtures.PortfolioFixtures;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PortfolioServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    void shouldCreatePortfolio() {
        UUID userId = UUID.randomUUID();

        portfolioService.createPortfolio(
                PortfolioEventFixtures.create(userId)
        );

        Portfolio portfolio = portfolioRepository.findById(userId)
                .orElseThrow();

        assertThat(portfolio.getUserId()).isEqualTo(userId);
        assertThat(portfolio.getNickname()).isNotBlank();
        assertThat(portfolio.getFirstName()).isEqualTo("John");
        assertThat(portfolio.getLastName()).isEqualTo("Doe");
        assertThat(portfolio.getBio()).isEqualTo("bio");
        assertThat(portfolio.getTotalProjects()).isZero();
    }

    @Test
    void shouldOverwriteExistingPortfolio() {
        UUID userId = UUID.randomUUID();

        portfolioRepository.save(
                PortfolioFixtures.old(userId)
        );

        portfolioService.createPortfolio(
                PortfolioEventFixtures.update(userId)
        );

        Portfolio updated = portfolioRepository.findById(userId)
                .orElseThrow();

        assertThat(updated.getNickname()).isEqualTo("newNick");
        assertThat(updated.getFirstName()).isEqualTo("New");
        assertThat(updated.getLastName()).isEqualTo("Name");
        assertThat(updated.getBio()).isEqualTo("newBio");
    }

    @Test
    void shouldDeletePortfolio() {
        UUID userId = UUID.randomUUID();

        portfolioRepository.save(
                PortfolioFixtures.valid(userId)
        );

        portfolioService.deletePortfolio(
                PortfolioEventFixtures.delete(userId)
        );

        assertThat(portfolioRepository.existsById(userId)).isFalse();
    }

    @Test
    void shouldThrow_ifNotFound() {
        UUID userId = UUID.randomUUID();

        assertThatThrownBy(() -> portfolioService.getPortfolio(userId))
                .isInstanceOf(PortfolioNotFoundException.class);
    }
}

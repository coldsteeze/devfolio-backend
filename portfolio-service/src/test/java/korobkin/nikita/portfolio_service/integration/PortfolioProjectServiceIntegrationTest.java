package korobkin.nikita.portfolio_service.integration;

import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import korobkin.nikita.portfolio_service.fixtures.PortfolioFixtures;
import korobkin.nikita.portfolio_service.fixtures.PortfolioProjectFixtures;
import korobkin.nikita.portfolio_service.fixtures.ProjectEventFixtures;
import korobkin.nikita.portfolio_service.repository.PortfolioProjectRepository;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.service.PortfolioProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class PortfolioProjectServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PortfolioProjectService service;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioProjectRepository projectRepository;

    @Test
    void shouldCreateProject_ifPublic() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = portfolioRepository.save(
                PortfolioFixtures.valid(userId)
        );

        UUID projectId = UUID.randomUUID();

        service.createPortfolioProject(
                ProjectEventFixtures.create(projectId, userId, true)
        );

        PortfolioProject project = projectRepository.findById(projectId)
                .orElseThrow();

        assertThat(project.getName()).isEqualTo("proj");
        assertThat(project.getPortfolio().getUserId()).isEqualTo(userId);

        Portfolio updated = portfolioRepository.findById(userId).orElseThrow();
        assertThat(updated.getTotalProjects()).isEqualTo((short) 1);
    }

    @Test
    void shouldIgnore_ifProjectNotPublic() {
        UUID userId = UUID.randomUUID();
        portfolioRepository.save(PortfolioFixtures.valid(userId));

        service.createPortfolioProject(
                ProjectEventFixtures.create(UUID.randomUUID(), userId, false)
        );

        assertThat(projectRepository.findAll()).isEmpty();
    }

    @Test
    void shouldIgnore_ifProjectAlreadyExists() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = portfolioRepository.save(
                PortfolioFixtures.valid(userId)
        );

        UUID projectId = UUID.randomUUID();

        projectRepository.save(
                PortfolioProjectFixtures.existing(projectId, portfolio)
        );

        service.createPortfolioProject(
                ProjectEventFixtures.create(projectId, userId, true)
        );

        PortfolioProject project = projectRepository.findById(projectId).orElseThrow();

        assertThat(project.getName()).isEqualTo("old");
    }

    @Test
    void shouldDeleteProject_ifBecomesPrivate() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = portfolioRepository.save(
                PortfolioFixtures.valid(userId)
        );

        UUID projectId = UUID.randomUUID();

        PortfolioProject project = projectRepository.save(
                PortfolioProjectFixtures.validPublic(projectId, portfolio)
        );

        portfolio.addProject(project);
        portfolioRepository.save(portfolio);

        service.updatePortfolioProject(
                ProjectEventFixtures.update(projectId, userId, false)
        );

        assertThat(projectRepository.findById(projectId)).isEmpty();

        Portfolio updated = portfolioRepository.findById(userId).orElseThrow();
        assertThat(updated.getTotalProjects()).isZero();
    }

    @Test
    void shouldCreateProject_ifNotExistsOnUpdate() {
        UUID userId = UUID.randomUUID();
        portfolioRepository.save(PortfolioFixtures.valid(userId));

        UUID projectId = UUID.randomUUID();

        service.updatePortfolioProject(
                ProjectEventFixtures.update(projectId, userId, true)
        );

        assertThat(projectRepository.findById(projectId)).isPresent();
    }

    @Test
    void shouldUpdateExistingProject() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = portfolioRepository.save(
                PortfolioFixtures.valid(userId)
        );

        UUID projectId = UUID.randomUUID();

        PortfolioProject project = projectRepository.save(
                PortfolioProjectFixtures.project(projectId, portfolio, "old", true)
        );

        service.updatePortfolioProject(
                ProjectEventFixtures.update(projectId, userId, true)
        );

        PortfolioProject updated = projectRepository.findById(projectId).orElseThrow();

        assertThat(updated.getName()).isEqualTo("newName");
        assertThat(updated.getDescription()).isEqualTo("newDesc");
        assertThat(updated.getGithubUrl()).isEqualTo("newGit");
    }

    @Test
    void shouldDeleteProject() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = portfolioRepository.save(
                PortfolioFixtures.valid(userId)
        );

        UUID projectId = UUID.randomUUID();

        PortfolioProject project = projectRepository.save(
                PortfolioProjectFixtures.validPublic(projectId, portfolio)
        );

        portfolio.addProject(project);
        portfolioRepository.save(portfolio);

        service.deletePortfolioProject(
                ProjectEventFixtures.delete(projectId)
        );

        assertThat(projectRepository.findById(projectId)).isEmpty();
    }
}

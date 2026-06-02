package korobkin.nikita.portfolio_service.integration;

import korobkin.nikita.events.ProjectSkillAddedEvent;
import korobkin.nikita.events.ProjectSkillDto;
import korobkin.nikita.events.ProjectSkillRemovedEvent;
import korobkin.nikita.events.ProjectSkillsUpdatedEvent;
import korobkin.nikita.events.skill.SkillCategory;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import korobkin.nikita.portfolio_service.entity.PortfolioProjectSkill;
import korobkin.nikita.portfolio_service.fixtures.PortfolioFixtures;
import korobkin.nikita.portfolio_service.fixtures.PortfolioProjectFixtures;
import korobkin.nikita.portfolio_service.fixtures.PortfolioProjectSkillFixtures;
import korobkin.nikita.portfolio_service.repository.PortfolioProjectRepository;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.service.PortfolioProjectSkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class PortfolioProjectSkillServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PortfolioProjectSkillService service;

    @Autowired
    private PortfolioProjectRepository projectRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;


    private PortfolioProject createProject() {
        Portfolio portfolio = portfolioRepository.save(
                PortfolioFixtures.valid(UUID.randomUUID())
        );

        return projectRepository.save(
                PortfolioProjectFixtures.project(UUID.randomUUID(), portfolio, "proj", true)
        );
    }


    @Test
    void shouldAddSkill() {
        PortfolioProject project = createProject();

        service.addPortfolioProjectSkill(
                new ProjectSkillAddedEvent(
                        UUID.randomUUID(),
                        project.getProjectId(),
                        "Java",
                        SkillCategory.LANGUAGE
                )
        );

        PortfolioProject updated = projectRepository.findById(project.getProjectId())
                .orElseThrow();

        assertThat(updated.getSkills())
                .extracting(PortfolioProjectSkill::getSkillName)
                .contains("Java");
    }


    @Test
    void shouldNotDuplicateSkill() {
        PortfolioProject project = createProject();

        service.addPortfolioProjectSkill(
                new ProjectSkillAddedEvent(UUID.randomUUID(), project.getProjectId(), "Java", SkillCategory.LANGUAGE)
        );

        service.addPortfolioProjectSkill(
                new ProjectSkillAddedEvent(UUID.randomUUID(), project.getProjectId(), "Java", SkillCategory.LANGUAGE)
        );

        PortfolioProject updated = projectRepository.findById(project.getProjectId())
                .orElseThrow();

        assertThat(updated.getSkills()).hasSize(1);
    }

    @Test
    void shouldRemoveSkill() {
        PortfolioProject project = createProject();

        project.addSkill(
                PortfolioProjectSkillFixtures.javaUnconfirmed()
        );
        projectRepository.save(project);

        service.deletePortfolioProjectSkill(
                new ProjectSkillRemovedEvent(UUID.randomUUID(), project.getProjectId(), "Java")
        );

        PortfolioProject updated = projectRepository.findById(project.getProjectId())
                .orElseThrow();

        assertThat(updated.getSkills()).isEmpty();
    }

    @Test
    void shouldUpdateSkillConfirmation() {
        PortfolioProject project = createProject();

        project.addSkill(
                PortfolioProjectSkillFixtures.javaUnconfirmed()
        );
        projectRepository.save(project);

        service.updatePortfolioProjectSkill(
                new ProjectSkillsUpdatedEvent(
                        UUID.randomUUID(),
                        project.getProjectId(),
                        List.of(new ProjectSkillDto("Java", true))
                )
        );

        PortfolioProject updated = projectRepository.findById(project.getProjectId())
                .orElseThrow();

        assertThat(updated.getSkills().iterator().next().isConfirmed()).isTrue();
    }
}

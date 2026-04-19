package korobkin.nikita.portfolio_service.fixtures;

import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.UUID;

@UtilityClass
public class PortfolioProjectFixtures {

    public static PortfolioProject project(
            UUID projectId,
            Portfolio portfolio,
            String name,
            boolean isPublic
    ) {
        PortfolioProject project = new PortfolioProject();
        project.setProjectId(projectId);
        project.setName(name);
        project.setDescription("desc");
        project.setGithubUrl("git");
        project.setProjectPublic(isPublic);
        project.setPortfolio(portfolio);
        project.setSkills(new HashSet<>());
        return project;
    }

    public static PortfolioProject validPublic(UUID projectId, Portfolio portfolio) {
        return project(projectId, portfolio, "proj", true);
    }

    public static PortfolioProject existing(UUID projectId, Portfolio portfolio) {
        return project(projectId, portfolio, "old", true);
    }

    public static PortfolioProject updated(UUID projectId, Portfolio portfolio) {
        return project(projectId, portfolio, "newName", true);
    }
}

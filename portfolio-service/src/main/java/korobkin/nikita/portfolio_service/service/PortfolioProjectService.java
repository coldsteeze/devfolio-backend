package korobkin.nikita.portfolio_service.service;

import korobkin.nikita.events.ProjectCreatedEvent;
import korobkin.nikita.events.ProjectDeletedEvent;
import korobkin.nikita.events.ProjectUpdatedEvent;

public interface PortfolioProjectService {

    void createPortfolioProject(ProjectCreatedEvent event);

    void updatePortfolioProject(ProjectUpdatedEvent event);

    void deletePortfolioProject(ProjectDeletedEvent event);
}

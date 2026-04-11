package korobkin.nikita.portfolio_service.service;

import korobkin.nikita.events.*;

public interface PortfolioService {

    void deletePortfolio(UserDeletedEvent event);

    void createPortfolio(UserProfileUpdatedEvent event);

    void createPortfolioProject(ProjectCreatedEvent event);

    void updatePortfolioProject(ProjectUpdatedEvent event);

    void deletePortfolioProject(ProjectDeletedEvent event);
}

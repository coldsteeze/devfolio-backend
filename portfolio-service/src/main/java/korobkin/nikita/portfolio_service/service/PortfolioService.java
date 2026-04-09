package korobkin.nikita.portfolio_service.service;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.events.UserProfileUpdatedEvent;

public interface PortfolioService {

    void deletePortfolio(UserDeletedEvent event);

    void createPortfolio(UserProfileUpdatedEvent event);
}

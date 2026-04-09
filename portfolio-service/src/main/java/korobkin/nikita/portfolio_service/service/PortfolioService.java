package korobkin.nikita.portfolio_service.service;

import korobkin.nikita.events.UserDeletedEvent;

public interface PortfolioService {

    void deletePortfolio(UserDeletedEvent event);
}

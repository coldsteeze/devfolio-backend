package korobkin.nikita.portfolio_service.service;

import korobkin.nikita.events.*;
import korobkin.nikita.portfolio_service.dto.PortfolioResponse;
import korobkin.nikita.portfolio_service.security.user.UserPrincipal;

import java.util.UUID;

public interface PortfolioService {

    void deletePortfolio(UserDeletedEvent event);

    void createPortfolio(UserProfileUpdatedEvent event);

    void addPortfolioProjectSkill(ProjectSkillAddedEvent event);

    void deletePortfolioProjectSkill(ProjectSkillRemovedEvent event);

    void updatePortfolioProjectSkill(ProjectSkillsUpdatedEvent event);

    PortfolioResponse getPortfolio(UUID userId);

    PortfolioResponse getMyPortfolio(UserPrincipal currentUser);
}

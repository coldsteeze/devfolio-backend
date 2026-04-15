package korobkin.nikita.portfolio_service.service;

import korobkin.nikita.events.ProjectSkillAddedEvent;
import korobkin.nikita.events.ProjectSkillRemovedEvent;
import korobkin.nikita.events.ProjectSkillsUpdatedEvent;

public interface PortfolioProjectSkillService {

    void addPortfolioProjectSkill(ProjectSkillAddedEvent event);

    void deletePortfolioProjectSkill(ProjectSkillRemovedEvent event);

    void updatePortfolioProjectSkill(ProjectSkillsUpdatedEvent event);
}

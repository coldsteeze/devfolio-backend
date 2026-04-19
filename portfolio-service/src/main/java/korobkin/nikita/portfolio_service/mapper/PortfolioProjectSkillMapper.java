package korobkin.nikita.portfolio_service.mapper;

import korobkin.nikita.events.ProjectSkillAddedEvent;
import korobkin.nikita.portfolio_service.entity.PortfolioProjectSkill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortfolioProjectSkillMapper {

    PortfolioProjectSkill toEntity(ProjectSkillAddedEvent event);
}

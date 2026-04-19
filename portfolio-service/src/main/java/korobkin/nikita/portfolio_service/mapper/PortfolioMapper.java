package korobkin.nikita.portfolio_service.mapper;

import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.dto.PortfolioProjectResponse;
import korobkin.nikita.portfolio_service.dto.PortfolioProjectSkillResponse;
import korobkin.nikita.portfolio_service.dto.PortfolioResponse;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import korobkin.nikita.portfolio_service.entity.PortfolioProjectSkill;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {

    Portfolio toEntity(UserProfileUpdatedEvent event);

    PortfolioResponse toResponse(Portfolio portfolio);

    List<PortfolioProjectResponse> toProjectResponses(List<PortfolioProject> projects);

    PortfolioProjectResponse toProjectResponse(PortfolioProject project);

    List<PortfolioProjectSkillResponse> toSkillResponses(Set<PortfolioProjectSkill> skills);

    PortfolioProjectSkillResponse toSkillResponse(PortfolioProjectSkill skill);
}

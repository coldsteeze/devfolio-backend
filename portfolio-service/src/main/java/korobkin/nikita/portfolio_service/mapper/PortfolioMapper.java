package korobkin.nikita.portfolio_service.mapper;

import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.dto.*;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioCareerEntry;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import korobkin.nikita.portfolio_service.entity.PortfolioProjectSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {

    @Mapping(target = "career", source = "careerEntries")
    PortfolioResponse toResponse(Portfolio portfolio);

    List<PortfolioProjectResponse> toProjectResponses(List<PortfolioProject> projects);

    PortfolioProjectResponse toProjectResponse(PortfolioProject project);

    List<PortfolioProjectSkillResponse> toSkillResponses(Set<PortfolioProjectSkill> skills);

    PortfolioProjectSkillResponse toSkillResponse(PortfolioProjectSkill skill);

    List<PortfolioCareerEntryResponse> toCareerResponses(List<PortfolioCareerEntry> entries);

    @Mapping(
            target = "startDate",
            expression = "java(mapDate(entry.getStartMonth(), entry.getStartYear()))"
    )
    @Mapping(
            target = "endDate",
            expression = "java(mapDate(entry.getEndMonth(), entry.getEndYear()))"
    )
    PortfolioCareerEntryResponse toCareerResponse(PortfolioCareerEntry entry);

    default PortfolioCareerDateResponse mapDate(Integer month, Integer year) {
        if (month == null || year == null) {
            return null;
        }
        return new PortfolioCareerDateResponse(month, year);
    }

    Portfolio toEntity(UserProfileUpdatedEvent event);
}

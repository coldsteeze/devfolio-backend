package korobkin.nikita.portfolio_service.mapper;

import korobkin.nikita.events.ProjectCreatedEvent;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortfolioProjectMapper {

    PortfolioProject toEntity(ProjectCreatedEvent event);
}

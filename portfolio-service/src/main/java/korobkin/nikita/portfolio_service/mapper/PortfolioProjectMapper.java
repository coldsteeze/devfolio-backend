package korobkin.nikita.portfolio_service.mapper;

import korobkin.nikita.events.ProjectCreatedEvent;
import korobkin.nikita.events.ProjectPreviewUpdatedEvent;
import korobkin.nikita.events.ProjectUpdatedEvent;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PortfolioProjectMapper {

    PortfolioProject toEntity(ProjectCreatedEvent event);

    PortfolioProject toEntity(ProjectUpdatedEvent event);

    void updateEntityFromEvent(ProjectUpdatedEvent event, @MappingTarget PortfolioProject project);

    void updateEntityFromEvent(ProjectPreviewUpdatedEvent event, @MappingTarget PortfolioProject project);
}

package korobkin.nikita.project_service.mapper;

import korobkin.nikita.events.ProjectCreatedEvent;
import korobkin.nikita.events.ProjectUpdatedEvent;
import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.dto.response.PagedResponse;
import korobkin.nikita.project_service.dto.response.ProjectDetailsResponse;
import korobkin.nikita.project_service.dto.response.ProjectResponse;
import korobkin.nikita.project_service.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ProjectSkillMapper.class, ProjectImageMapper.class})
public interface ProjectMapper {

    @Mapping(target = "projectPublic", source = "projectPublic")
    ProjectResponse toDto(Project project);

    @Mapping(target = "pageNumber", source = "number")
    @Mapping(target = "pageSize", source = "size")
    PagedResponse<ProjectResponse> toPagedDto(Page<Project> projectsPage);

    void updateEntityFromDto(UpdateProjectRequest request, @MappingTarget Project project);

    @Mapping(target = "project", source = "project")
    @Mapping(target = "skills", source = "skills")
    @Mapping(target = "images", source = "images")
    ProjectDetailsResponse toDetailsDto(Project project);

    @Mapping(target = "projectId", source = "id")
    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    ProjectCreatedEvent toProjectCreatedEvent(Project project);

    @Mapping(target = "projectId", source = "id")
    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID())")
    ProjectUpdatedEvent toProjectUpdatedEvent(Project project);

    Project toEntity(CreateProjectRequest request);
}

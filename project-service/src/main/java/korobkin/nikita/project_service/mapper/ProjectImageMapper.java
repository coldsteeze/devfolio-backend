package korobkin.nikita.project_service.mapper;

import korobkin.nikita.project_service.dto.response.ProjectImageResponse;
import korobkin.nikita.project_service.entity.ProjectImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectImageMapper {

    @Mapping(source = "project.id", target = "projectId")
    ProjectImageResponse toDto(ProjectImage projectImage);
}

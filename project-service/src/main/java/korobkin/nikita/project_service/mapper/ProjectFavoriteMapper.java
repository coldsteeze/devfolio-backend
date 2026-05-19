package korobkin.nikita.project_service.mapper;

import korobkin.nikita.project_service.dto.response.ProjectFavoriteResponse;
import korobkin.nikita.project_service.entity.ProjectFavorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectFavoriteMapper {

    @Mapping(source = "id", target = "favoriteId")
    ProjectFavoriteResponse toDto(ProjectFavorite projectFavorite);
}

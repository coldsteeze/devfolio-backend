package korobkin.nikita.project_service.mapper;

import korobkin.nikita.project_service.dto.response.ProjectSkillResponse;
import korobkin.nikita.project_service.entity.ProjectSkill;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectSkillMapper {

    ProjectSkillResponse toDto(ProjectSkill projectSkill);

    List<ProjectSkillResponse> toDtoList(List<ProjectSkill> projectSkillList);
}

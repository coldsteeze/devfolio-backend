package korobkin.nikita.project_service.mapper;

import korobkin.nikita.events.ProjectSkillAddedEvent;
import korobkin.nikita.events.ProjectSkillDto;
import korobkin.nikita.project_service.dto.response.ProjectSkillResponse;
import korobkin.nikita.project_service.dto.response.skill.SkillResponse;
import korobkin.nikita.project_service.entity.ProjectSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectSkillMapper {

    ProjectSkillResponse toDto(ProjectSkill projectSkill);

    List<ProjectSkillResponse> toDtoList(List<ProjectSkill> projectSkillList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "skillId", source = "id")
    @Mapping(target = "skillName", source = "name")
    @Mapping(target = "skillCategory", source = "category")
    ProjectSkill toEntity(SkillResponse response);

    @Mapping(target = "projectId", source = "project.id")
    ProjectSkillAddedEvent toProjectSkillAddedEvent(ProjectSkill projectSkill);

    List<ProjectSkillDto> toProjectSkillDto(List<ProjectSkill> skills);
}

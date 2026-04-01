package korobkin.nikita.project_service.mapper;

import korobkin.nikita.events.skill.SkillShortInfo;
import korobkin.nikita.project_service.entity.ProjectSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillEventMapper {

    @Mapping(target = "projectSkillId", source = "id")
    @Mapping(target = "skillId", source = "skillId")
    @Mapping(target = "name", source = "skillName")
    @Mapping(target = "category", source = "skillCategory")
    SkillShortInfo toEvent(ProjectSkill projectSkill);

    List<SkillShortInfo> toEventList(List<ProjectSkill> projectSkills);
}

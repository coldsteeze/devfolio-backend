package korobkin.nikita.skill_service.mapper;

import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.dto.response.SkillResponse;
import korobkin.nikita.skill_service.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(source = "id", target = "id")
    SkillResponse toDto(Skill skill);

    Skill toEntity(CreateSkillRequest request);

    void updateEntityFromDto(UpdateSkillRequest request, @MappingTarget Skill skill);
}

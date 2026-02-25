package korobkin.nikita.skill_service.fixtures;

import korobkin.nikita.skill_service.dto.response.PagedResponse;
import korobkin.nikita.skill_service.dto.response.SkillResponse;
import korobkin.nikita.skill_service.entity.Skill;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class SkillResponseFixtures {
    
    public static SkillResponse skillResponse(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName(), skill.getCategory());
    }

    public static PagedResponse<SkillResponse> pagedResponse(
            List<SkillResponse> content,
            int pageNumber,
            int pageSize,
            long totalElements,
            int totalPages) {
        return new PagedResponse<>(content, pageNumber, pageSize, totalElements, totalPages);
    }
}

package korobkin.nikita.skill_service.fixtures;

import korobkin.nikita.skill_service.dto.request.BulkSkillRequest;
import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
import korobkin.nikita.skill_service.dto.request.SkillFilterRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.entity.Skill;
import korobkin.nikita.skill_service.entity.enums.SkillCategory;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class SkillRequestFixtures {

    public static BulkSkillRequest bulkSkillRequest(List<String> skillIds) {
        BulkSkillRequest bulkSkillRequest = new BulkSkillRequest();
        bulkSkillRequest.setSkillIds(skillIds);

        return bulkSkillRequest;
    }

    public static CreateSkillRequest createSkillRequest(Skill skill) {
        CreateSkillRequest createSkillRequest = new CreateSkillRequest();
        createSkillRequest.setName(skill.getName());
        createSkillRequest.setCategory(skill.getCategory());

        return createSkillRequest;
    }

    public static UpdateSkillRequest updateSkillRequest(Skill skill) {
        UpdateSkillRequest updateSkillRequest = new UpdateSkillRequest();
        updateSkillRequest.setName(skill.getName());
        updateSkillRequest.setCategory(skill.getCategory());

        return updateSkillRequest;
    }

    public static SkillFilterRequest skillFilterRequest(String search, SkillCategory skillCategory) {
        SkillFilterRequest skillFilterRequest = new SkillFilterRequest();
        skillFilterRequest.setSearch(search);
        skillFilterRequest.setCategory(skillCategory);

        return skillFilterRequest;
    }
}

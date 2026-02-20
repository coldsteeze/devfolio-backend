package korobkin.nikita.skill_service.fixtures;

import korobkin.nikita.skill_service.dto.request.BulkSkillRequest;
import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.entity.Skill;
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
}

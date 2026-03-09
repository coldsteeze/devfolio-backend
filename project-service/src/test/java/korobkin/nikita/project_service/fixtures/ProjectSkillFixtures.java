package korobkin.nikita.project_service.fixtures;

import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ProjectSkillFixtures {

    public static ProjectSkill validProjectSkill(Project project, UUID skillId) {
        return projectSkill(project, skillId, true, false);
    }

    public static ProjectSkill projectSkill(Project project, UUID skillId, boolean manuallyAdded, boolean confirmed) {
        ProjectSkill projectSkill = new ProjectSkill();
        projectSkill.setProject(project);
        projectSkill.setSkillId(skillId);
        projectSkill.setManuallyAdded(manuallyAdded);
        projectSkill.setConfirmed(confirmed);

        return projectSkill;
    }
}

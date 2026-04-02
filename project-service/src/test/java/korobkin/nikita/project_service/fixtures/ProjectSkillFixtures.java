package korobkin.nikita.project_service.fixtures;

import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;
import korobkin.nikita.project_service.entity.enums.SkillCategory;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ProjectSkillFixtures {

    public static ProjectSkill validProjectSkill(Project project, UUID skillId) {
        return projectSkill(project, skillId, "Java", SkillCategory.LANGUAGE, true, false);
    }

    public static ProjectSkill projectSkill(
            Project project,
            UUID skillId,
            String skillName,
            SkillCategory skillCategory,
            boolean manuallyAdded,
            boolean confirmed) {
        ProjectSkill projectSkill = new ProjectSkill();
        projectSkill.setProject(project);
        projectSkill.setSkillId(skillId);
        projectSkill.setSkillName(skillName);
        projectSkill.setSkillCategory(skillCategory);
        projectSkill.setManuallyAdded(manuallyAdded);
        projectSkill.setConfirmed(confirmed);

        return projectSkill;
    }
}

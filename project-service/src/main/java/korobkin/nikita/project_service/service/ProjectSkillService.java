package korobkin.nikita.project_service.service;

import korobkin.nikita.project_service.dto.response.ProjectSkillResponse;
import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;

import java.util.List;
import java.util.UUID;

public interface ProjectSkillService {

    ProjectSkillResponse addForProject(Project project, UUID skillId, boolean manuallyAdded);

    void deleteForProject(Project project, UUID skillId);

    void confirmForProject(Project project, UUID skillId);

    List<ProjectSkillResponse> getProjectSkills(Project project);

    ProjectSkill findProjectSkillByProjectAndSkill(Project project, UUID skillId);
}

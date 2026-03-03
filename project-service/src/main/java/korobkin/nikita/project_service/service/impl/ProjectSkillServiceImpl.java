package korobkin.nikita.project_service.service.impl;

import korobkin.nikita.project_service.dto.response.ProjectSkillResponse;
import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;
import korobkin.nikita.project_service.exception.ErrorCode;
import korobkin.nikita.project_service.exception.ProjectSkillAlreadyExistsException;
import korobkin.nikita.project_service.exception.ProjectSkillNotFoundException;
import korobkin.nikita.project_service.mapper.ProjectSkillMapper;
import korobkin.nikita.project_service.repository.ProjectSkillRepository;
import korobkin.nikita.project_service.service.ProjectSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillServiceImpl implements ProjectSkillService {

    private final ProjectSkillRepository projectSkillRepository;
    private final ProjectSkillMapper projectSkillMapper;

    @Override
    public ProjectSkillResponse addForProject(Project project, UUID skillId, boolean manuallyAdded) {
        if (projectSkillRepository.existsByProjectAndSkillId(project, skillId)) {
            throw new ProjectSkillAlreadyExistsException(ErrorCode.PROJECT_SKILL_ALREADY_EXISTS);
        }

        ProjectSkill projectSkill = new ProjectSkill();
        projectSkill.setSkillId(skillId);
        projectSkill.setProject(project);
        projectSkill.setManuallyAdded(true);

        projectSkillRepository.save(projectSkill);
        log.info("Project skill with id {} saved in repository", projectSkill.getId());

        return projectSkillMapper.toDto(projectSkill);
    }

    @Override
    public void deleteForProject(Project project, UUID skillId) {
        ProjectSkill projectSkill = projectSkillRepository.findProjectSkillByProjectAndSkillId(project, skillId)
                .orElseThrow(() -> new ProjectSkillNotFoundException(ErrorCode.PROJECT_SKILL_NOT_FOUND));

        projectSkillRepository.delete(projectSkill);
        log.info("Successfully delete skill for project with id {}", project.getId());
    }

    @Override
    public void confirmForProject(Project project, UUID skillId) {
        ProjectSkill projectSkill = projectSkillRepository.findProjectSkillByProjectAndSkillId(project, skillId)
                .orElseThrow(() -> new ProjectSkillNotFoundException(ErrorCode.PROJECT_SKILL_NOT_FOUND));

        projectSkill.setConfirmed(true);
        log.info("Confirm skill for project with id {}", project.getId());
    }

    @Override
    public List<ProjectSkillResponse> getProjectSkills(Project project) {
        List<ProjectSkill> projectSkills = projectSkillRepository.findProjectSkillsByProject(project);

        log.info("Get project skills by project id {}", project.getId());

        return projectSkillMapper.toDtoList(projectSkills);
    }
}

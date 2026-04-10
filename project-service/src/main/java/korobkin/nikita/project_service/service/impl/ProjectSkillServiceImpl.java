package korobkin.nikita.project_service.service.impl;

import feign.FeignException;
import korobkin.nikita.events.ProjectSkillRemovedEvent;
import korobkin.nikita.project_service.client.SkillClient;
import korobkin.nikita.project_service.dto.response.ProjectSkillResponse;
import korobkin.nikita.project_service.dto.response.skill.SkillResponse;
import korobkin.nikita.project_service.entity.Project;
import korobkin.nikita.project_service.entity.ProjectSkill;
import korobkin.nikita.project_service.exception.ErrorCode;
import korobkin.nikita.project_service.exception.ProjectSkillAlreadyExistsException;
import korobkin.nikita.project_service.exception.ProjectSkillNotFoundException;
import korobkin.nikita.project_service.exception.SkillNotFoundException;
import korobkin.nikita.project_service.kafka.producer.ProjectSkillAddedEventProducer;
import korobkin.nikita.project_service.kafka.producer.ProjectSkillRemovedEventProducer;
import korobkin.nikita.project_service.mapper.ProjectSkillMapper;
import korobkin.nikita.project_service.repository.ProjectSkillRepository;
import korobkin.nikita.project_service.service.ProjectSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectSkillServiceImpl implements ProjectSkillService {

    private final ProjectSkillRepository projectSkillRepository;
    private final ProjectSkillMapper projectSkillMapper;
    private final SkillClient skillClient;
    private final ProjectSkillAddedEventProducer projectSkillAddedEventProducer;
    private final ProjectSkillRemovedEventProducer projectSkillRemovedEventProducer;

    @Override
    public ProjectSkillResponse addForProject(Project project, UUID skillId, boolean manuallyAdded) {
        if (projectSkillRepository.existsByProjectAndSkillId(project, skillId)) {
            throw new ProjectSkillAlreadyExistsException(ErrorCode.PROJECT_SKILL_ALREADY_EXISTS);
        }

        try {
            SkillResponse response = skillClient.getSkillById(skillId);
            ProjectSkill projectSkill = projectSkillMapper.toEntity(response);
            projectSkill.setProject(project);
            projectSkill.setManuallyAdded(manuallyAdded);

            projectSkillRepository.saveAndFlush(projectSkill);
            log.info("Project skill with id {} saved in repository", projectSkill.getId());

            projectSkillAddedEventProducer.sendProjectSkillAdded(
                    projectSkillMapper.toProjectSkillAddedEvent(projectSkill)
            );

            return projectSkillMapper.toDto(projectSkill);
        } catch (FeignException.NotFound ex) {
            throw new SkillNotFoundException(ErrorCode.SKILL_NOT_FOUND);
        } catch (DataIntegrityViolationException ex) {
            throw new ProjectSkillAlreadyExistsException(ErrorCode.PROJECT_SKILL_ALREADY_EXISTS);
        }
    }

    @Override
    public void deleteForProject(Project project, UUID skillId) {
        ProjectSkill projectSkill = projectSkillRepository.findProjectSkillByProjectAndSkillId(project, skillId)
                .orElseThrow(() -> new ProjectSkillNotFoundException(ErrorCode.PROJECT_SKILL_NOT_FOUND));

        projectSkillRepository.delete(projectSkill);
        log.info("Successfully delete skill for project with id {}", project.getId());

        projectSkillRemovedEventProducer.sendProjectSkillRemoved(
                new ProjectSkillRemovedEvent(projectSkill.getProject().getId(), projectSkill.getSkillName())
        );
    }

    @Override
    public void confirmProjectSkill(UUID projectSkillId) {
        ProjectSkill projectSkill = projectSkillRepository.findById(projectSkillId)
                .orElseThrow(() -> new ProjectSkillNotFoundException(ErrorCode.PROJECT_SKILL_NOT_FOUND));

        projectSkill.setConfirmed(true);
        log.info("Confirm skill for project with id {}", projectSkill.getProject().getId());
    }

    @Override
    public List<ProjectSkillResponse> getProjectSkills(Project project) {
        log.info("Get project skills by project id {}", project.getId());

        return projectSkillMapper.toDtoList(
                projectSkillRepository.findByProjectId(project.getId())
        );
    }

    @Override
    public List<ProjectSkill> findProjectSkillsByProject(Project project) {
        return project.getSkills();
    }
}

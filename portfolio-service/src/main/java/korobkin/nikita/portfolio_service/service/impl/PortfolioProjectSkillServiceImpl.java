package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.events.ProjectSkillAddedEvent;
import korobkin.nikita.events.ProjectSkillRemovedEvent;
import korobkin.nikita.events.ProjectSkillsUpdatedEvent;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import korobkin.nikita.portfolio_service.exception.ErrorCode;
import korobkin.nikita.portfolio_service.exception.PortfolioProjectNotFoundException;
import korobkin.nikita.portfolio_service.mapper.PortfolioProjectSkillMapper;
import korobkin.nikita.portfolio_service.repository.PortfolioProjectRepository;
import korobkin.nikita.portfolio_service.service.PortfolioProjectSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioProjectSkillServiceImpl implements PortfolioProjectSkillService {

    private final PortfolioProjectRepository projectRepository;
    private final PortfolioProjectSkillMapper mapper;

    @Override
    @Transactional
    public void addPortfolioProjectSkill(ProjectSkillAddedEvent event) {
        PortfolioProject project = getProject(event.projectId());

        project.addSkill(mapper.toEntity(event));

        log.info("Skill added: {}", event.skillName());
    }

    @Override
    @Transactional
    public void deletePortfolioProjectSkill(ProjectSkillRemovedEvent event) {
        PortfolioProject project = getProject(event.projectId());

        project.removeSkill(event.name());

        log.info("Skill removed: {}", event.name());
    }

    @Override
    @Transactional
    public void updatePortfolioProjectSkill(ProjectSkillsUpdatedEvent event) {
        PortfolioProject project = getProject(event.projectId());

        project.updateSkills(event.skills());

        log.info("Skills updated: {}", event.projectId());
    }

    private PortfolioProject getProject(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new PortfolioProjectNotFoundException(
                        ErrorCode.PORTFOLIO_PROJECT_NOT_FOUND
                ));
    }
}

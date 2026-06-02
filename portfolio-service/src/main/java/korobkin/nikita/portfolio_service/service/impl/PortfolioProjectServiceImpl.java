package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.events.ProjectCreatedEvent;
import korobkin.nikita.events.ProjectDeletedEvent;
import korobkin.nikita.events.ProjectUpdatedEvent;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import korobkin.nikita.portfolio_service.exception.ErrorCode;
import korobkin.nikita.portfolio_service.exception.PortfolioNotFoundException;
import korobkin.nikita.portfolio_service.mapper.PortfolioProjectMapper;
import korobkin.nikita.portfolio_service.repository.PortfolioProjectRepository;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.service.PortfolioProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioProjectServiceImpl implements PortfolioProjectService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioProjectRepository projectRepository;
    private final PortfolioProjectMapper mapper;

    @Override
    @Transactional
    public void createPortfolioProject(ProjectCreatedEvent event) {
        if (!event.projectPublic()) return;

        Portfolio portfolio = getPortfolio(event.userId());

        if (projectRepository.existsById(event.projectId())) return;

        PortfolioProject project = mapper.toEntity(event);

        portfolio.addProject(project);

        portfolioRepository.save(portfolio);

        log.info("Project created: {}", event.projectId());
    }

    @Override
    @Transactional
    public void updatePortfolioProject(ProjectUpdatedEvent event) {
        Portfolio portfolio = getPortfolio(event.userId());
        PortfolioProject existing = projectRepository.findById(event.projectId()).orElse(null);

        if (!event.projectPublic()) {
            if (existing != null) {
                portfolio.removeProject(existing.getProjectId());
                projectRepository.delete(existing);
            }
            return;
        }

        if (existing == null) {
            PortfolioProject project = mapper.toEntity(event);
            portfolio.addProject(project);
            portfolioRepository.save(portfolio);
            return;
        }

        mapper.updateEntityFromEvent(event, existing);
    }

    @Override
    @Transactional
    public void deletePortfolioProject(ProjectDeletedEvent event) {
        PortfolioProject project = projectRepository.findById(event.projectId()).orElse(null);

        if (project == null) return;

        Portfolio portfolio = project.getPortfolio();

        portfolio.removeProject(project.getProjectId());

        projectRepository.delete(project);

        log.info("Project deleted: {}", event.projectId());
    }

    private Portfolio getPortfolio(UUID userId) {
        return portfolioRepository.findById(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(
                        ErrorCode.PORTFOLIO_NOT_FOUND
                ));
    }
}

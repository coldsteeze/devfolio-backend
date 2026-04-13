package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.events.*;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import korobkin.nikita.portfolio_service.entity.PortfolioProjectSkill;
import korobkin.nikita.portfolio_service.mapper.PortfolioMapper;
import korobkin.nikita.portfolio_service.mapper.PortfolioProjectMapper;
import korobkin.nikita.portfolio_service.mapper.PortfolioProjectSkillMapper;
import korobkin.nikita.portfolio_service.repository.PortfolioProjectRepository;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioProjectRepository portfolioProjectRepository;
    private final PortfolioMapper portfolioMapper;
    private final PortfolioProjectMapper portfolioProjectMapper;
    private final PortfolioProjectSkillMapper portfolioProjectSkillMapper;

    @Override
    @Transactional
    public void deletePortfolio(UserDeletedEvent event) {
        portfolioRepository.deleteById(event.userId());
        log.info("Delete portfolio with userId: {}", event.userId());
    }

    @Override
    @Transactional
    public void createPortfolio(UserProfileUpdatedEvent event) {
        portfolioRepository.save(portfolioMapper.toEntity(event));
        log.info("Save portfolio with userId: {}", event.userId());
    }

    @Override
    @Transactional
    public void createPortfolioProject(ProjectCreatedEvent event) {
        if (portfolioProjectRepository.existsById(event.projectId())) {
            log.warn("Project already exists in portfolio: {}", event.projectId());
            return;
        }

        if (!event.projectPublic()) {
            log.info("Skip non-public project: {}", event.projectId());
            return;
        }

        Portfolio portfolio = portfolioRepository.findById(event.userId())
                .orElse(null);

        if (portfolio == null) {
            log.error("Portfolio not found for userId: {}", event.userId());
            return;
        }

        PortfolioProject portfolioProject = portfolioProjectMapper.toEntity(event);
        portfolioProject.setPortfolio(portfolio);

        portfolioProjectRepository.save(portfolioProject);

        portfolio.setTotalProjects((short) (portfolio.getTotalProjects() + 1));

        log.info("Saved portfolio project: {}", event.projectId());
    }

    @Override
    @Transactional
    public void updatePortfolioProject(ProjectUpdatedEvent event) {
        PortfolioProject existing = portfolioProjectRepository
                .findById(event.projectId())
                .orElse(null);

        Portfolio portfolio = portfolioRepository.findById(event.userId())
                .orElse(null);

        if (portfolio == null) {
            log.error("Portfolio not found: {}", event.userId());
            return;
        }

        if (!event.projectPublic()) {
            if (existing != null) {
                portfolioProjectRepository.delete(existing);
                portfolio.setTotalProjects((short) Math.max(0, portfolio.getTotalProjects() - 1));
                log.info("Deleted portfolio project: {}", event.projectId());
            }
            return;
        }

        if (existing == null) {
            PortfolioProject portfolioProject = portfolioProjectMapper.toEntity(event);
            portfolioProject.setPortfolio(portfolio);

            portfolioProjectRepository.save(portfolioProject);
            portfolio.setTotalProjects((short) (portfolio.getTotalProjects() + 1));

            log.info("Created portfolio project: {}", event.projectId());
            return;
        }

        portfolioProjectMapper.updateEntityFromEvent(event, existing);

        log.info("Updated portfolio project: {}", event.projectId());
    }

    @Override
    @Transactional
    public void deletePortfolioProject(ProjectDeletedEvent event) {
        PortfolioProject existing = portfolioProjectRepository
                .findById(event.projectId())
                .orElse(null);

        if (existing == null) {
            log.warn("Portfolio project not found: {}", event.projectId());
            return;
        }

        Portfolio portfolio = existing.getPortfolio();

        portfolioProjectRepository.delete(existing);

        portfolio.setTotalProjects(
                (short) Math.max(0, portfolio.getTotalProjects() - 1)
        );

        log.info("Deleted portfolio project: {}", event.projectId());
    }

    @Override
    @Transactional
    public void addPortfolioProjectSkill(ProjectSkillAddedEvent event) {
        PortfolioProject existing = portfolioProjectRepository
                .findById(event.projectId())
                .orElse(null);

        if (existing == null) {
            log.warn("Portfolio project not found: {}", event.projectId());
            return;
        }

        PortfolioProjectSkill projectSkill = portfolioProjectSkillMapper.toEntity(event);
        existing.getSkills().add(projectSkill);

        log.info("Add portfolio project skill {} in project: {}", event.skillName(), event.projectId());
    }
}

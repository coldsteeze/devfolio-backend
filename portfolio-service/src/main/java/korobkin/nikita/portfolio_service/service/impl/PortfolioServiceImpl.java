package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.events.*;
import korobkin.nikita.portfolio_service.dto.PortfolioResponse;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import korobkin.nikita.portfolio_service.entity.PortfolioProjectSkill;
import korobkin.nikita.portfolio_service.mapper.PortfolioMapper;
import korobkin.nikita.portfolio_service.mapper.PortfolioProjectSkillMapper;
import korobkin.nikita.portfolio_service.repository.PortfolioProjectRepository;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.security.user.UserPrincipal;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioProjectRepository portfolioProjectRepository;
    private final PortfolioMapper portfolioMapper;
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

    @Override
    @Transactional
    public void deletePortfolioProjectSkill(ProjectSkillRemovedEvent event) {
        PortfolioProject existing = portfolioProjectRepository
                .findById(event.projectId())
                .orElse(null);

        if (existing == null) {
            log.warn("Portfolio project not found: {}", event.projectId());
            return;
        }

        existing.getSkills().remove(
                new PortfolioProjectSkill(event.name(), null, false)
        );

        log.info("Remove portfolio project skill {} in project: {}", event.name(), event.projectId());
    }

    @Override
    @Transactional
    public void updatePortfolioProjectSkill(ProjectSkillsUpdatedEvent event) {
        PortfolioProject existing = portfolioProjectRepository
                .findById(event.projectId())
                .orElse(null);

        if (existing == null) {
            log.warn("Portfolio project not found: {}", event.projectId());
            return;
        }

        Map<String, PortfolioProjectSkill> existingSkills = existing.getSkills().stream()
                .collect(Collectors.toMap(
                        PortfolioProjectSkill::getSkillName,
                        Function.identity()
                ));

        for (ProjectSkillDto dto : event.skills()) {
            PortfolioProjectSkill skill = existingSkills.get(dto.skillName());

            if (skill != null) {
                skill.setConfirmed(dto.confirmed());
            }
        }

        log.info("Updated skill confirmations for project {}", event.projectId());
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio(UUID userId) {
        Portfolio portfolio = portfolioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        return portfolioMapper.toResponse(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getMyPortfolio(UserPrincipal currentUser) {
        Portfolio portfolio = portfolioRepository.findById(currentUser.userId())
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        return portfolioMapper.toResponse(portfolio);
    }
}

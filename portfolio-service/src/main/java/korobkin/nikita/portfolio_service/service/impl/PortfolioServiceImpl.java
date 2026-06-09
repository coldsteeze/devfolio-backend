package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.events.UserProfileAvatarUpdatedEvent;
import korobkin.nikita.events.UserProfileCareerUpdatedEvent;
import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.dto.PortfolioResponse;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.entity.PortfolioCareerEntry;
import korobkin.nikita.portfolio_service.exception.ErrorCode;
import korobkin.nikita.portfolio_service.exception.PortfolioNotFoundException;
import korobkin.nikita.portfolio_service.mapper.CareerEventMapper;
import korobkin.nikita.portfolio_service.mapper.PortfolioMapper;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.security.user.UserPrincipal;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    private final CareerEventMapper careerEventMapper;

    @Override
    @Transactional
    public void createPortfolio(UserProfileUpdatedEvent event) {
        portfolioRepository.save(portfolioMapper.toEntity(event));
        log.info("Portfolio created: {}", event.userId());
    }

    @Override
    @Transactional
    public void deletePortfolio(UserDeletedEvent event) {
        portfolioRepository.deleteById(event.userId());
        log.info("Portfolio deleted: {}", event.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio(UUID userId) {
        return portfolioMapper.toResponse(getOrThrow(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getMyPortfolio(UserPrincipal user) {
        return portfolioMapper.toResponse(getOrThrow(user.userId()));
    }

    @Override
    @Transactional
    public void updatePortfolioAvatar(UserProfileAvatarUpdatedEvent event) {
        Portfolio portfolio = getOrThrow(event.userId());
        portfolio.setAvatarUrl(event.avatarUrl());

        log.info("Portfolio avatar updated: {}", event.userId());
    }

    @Override
    @Transactional
    public void updatePortfolioCareerEntry(UserProfileCareerUpdatedEvent event) {
        log.info(
                "Updating portfolio career entries for userId={} with {} entries",
                event.userId(),
                event.career().size()
        );

        Portfolio portfolio = getOrThrow(event.userId());

        int oldEntriesCount = portfolio.getCareerEntries().size();

        portfolio.getCareerEntries().clear();

        log.debug(
                "Removed {} existing career entries for userId={}",
                oldEntriesCount,
                event.userId()
        );

        List<PortfolioCareerEntry> entries =
                careerEventMapper.toEntities(event.career());

        entries.forEach(entry -> entry.setPortfolio(portfolio));

        portfolio.getCareerEntries().addAll(entries);

        portfolioRepository.save(portfolio);

        log.info(
                "Successfully updated portfolio career entries for userId={}. Old entries: {}, new entries: {}",
                event.userId(),
                oldEntriesCount,
                entries.size()
        );
    }

    private Portfolio getOrThrow(UUID userId) {
        return portfolioRepository.findById(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(
                        ErrorCode.PORTFOLIO_NOT_FOUND
                ));
    }
}

package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.dto.PortfolioResponse;
import korobkin.nikita.portfolio_service.entity.Portfolio;
import korobkin.nikita.portfolio_service.exception.ErrorCode;
import korobkin.nikita.portfolio_service.exception.PortfolioNotFoundException;
import korobkin.nikita.portfolio_service.mapper.PortfolioMapper;
import korobkin.nikita.portfolio_service.repository.PortfolioRepository;
import korobkin.nikita.portfolio_service.security.user.UserPrincipal;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;

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

    private Portfolio getOrThrow(UUID userId) {
        return portfolioRepository.findById(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(
                        ErrorCode.PORTFOLIO_NOT_FOUND
                ));
    }
}

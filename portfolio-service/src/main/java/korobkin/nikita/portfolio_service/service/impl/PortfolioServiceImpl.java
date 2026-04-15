package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.dto.PortfolioResponse;
import korobkin.nikita.portfolio_service.entity.Portfolio;
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

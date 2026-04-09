package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.events.UserProfileUpdatedEvent;
import korobkin.nikita.portfolio_service.mapper.PortfolioMapper;
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
    private final PortfolioMapper portfolioMapper;

    @Override
    @Transactional
    public void deletePortfolio(UserDeletedEvent event) {
        portfolioRepository.deleteById(event.userId());
        log.info("Delete portfolio with userId: {}", event.userId());
    }

    @Override
    public void createPortfolio(UserProfileUpdatedEvent event) {
        portfolioRepository.save(portfolioMapper.toEntity(event));
        log.info("Save portfolio with userId: {}", event.userId());
    }
}

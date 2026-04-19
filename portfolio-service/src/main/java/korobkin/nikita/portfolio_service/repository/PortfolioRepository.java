package korobkin.nikita.portfolio_service.repository;

import korobkin.nikita.portfolio_service.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
}

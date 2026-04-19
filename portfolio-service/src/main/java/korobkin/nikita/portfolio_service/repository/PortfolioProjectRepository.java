package korobkin.nikita.portfolio_service.repository;

import korobkin.nikita.portfolio_service.entity.PortfolioProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortfolioProjectRepository extends JpaRepository<PortfolioProject, UUID> {
}

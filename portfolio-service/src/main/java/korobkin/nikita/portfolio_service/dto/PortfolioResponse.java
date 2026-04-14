package korobkin.nikita.portfolio_service.dto;

import java.util.List;
import java.util.UUID;

public record PortfolioResponse(
        UUID userId,
        String nickname,
        String firstName,
        String lastName,
        String bio,
        short totalProjects,
        List<PortfolioProjectResponse> projects
) {}

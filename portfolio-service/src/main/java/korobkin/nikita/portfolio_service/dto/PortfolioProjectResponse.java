package korobkin.nikita.portfolio_service.dto;

import java.util.List;
import java.util.UUID;

public record PortfolioProjectResponse(
        UUID projectId,
        String name,
        String description,
        String githubUrl,
        boolean projectPublic,
        List<PortfolioProjectSkillResponse> skills
) {}

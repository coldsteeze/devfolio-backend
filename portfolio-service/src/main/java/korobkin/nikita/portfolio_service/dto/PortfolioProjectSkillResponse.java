package korobkin.nikita.portfolio_service.dto;

import korobkin.nikita.portfolio_service.entity.enums.SkillCategory;

public record PortfolioProjectSkillResponse(
        String skillName,
        SkillCategory skillCategory,
        boolean confirmed
) {}

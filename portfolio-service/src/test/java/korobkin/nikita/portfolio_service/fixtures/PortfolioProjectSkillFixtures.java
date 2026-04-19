package korobkin.nikita.portfolio_service.fixtures;

import korobkin.nikita.portfolio_service.entity.PortfolioProjectSkill;
import korobkin.nikita.portfolio_service.entity.enums.SkillCategory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PortfolioProjectSkillFixtures {

    public static PortfolioProjectSkill skill(String name, SkillCategory category, boolean confirmed) {
        return new PortfolioProjectSkill(name, category, confirmed);
    }

    public static PortfolioProjectSkill javaUnconfirmed() {
        return skill("Java", SkillCategory.LANGUAGE, false);
    }

    public static PortfolioProjectSkill javaConfirmed() {
        return skill("Java", SkillCategory.LANGUAGE, true);
    }
}

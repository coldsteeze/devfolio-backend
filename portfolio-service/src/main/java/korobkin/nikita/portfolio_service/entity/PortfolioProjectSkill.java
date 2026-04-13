package korobkin.nikita.portfolio_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import korobkin.nikita.portfolio_service.entity.enums.SkillCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioProjectSkill {

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "skill_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillCategory skillCategory;

    @Column(name = "confirmed")
    private boolean confirmed;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioProjectSkill that = (PortfolioProjectSkill) o;
        return Objects.equals(skillName, that.skillName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(skillName);
    }
}

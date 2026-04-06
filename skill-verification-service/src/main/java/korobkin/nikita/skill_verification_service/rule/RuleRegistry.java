package korobkin.nikita.skill_verification_service.rule;

import korobkin.nikita.events.skill.SkillShortInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RuleRegistry {

    private final List<SkillVerificationRule> rules;

    public RuleRegistry(List<SkillVerificationRule> rules) {
        this.rules = rules;
    }

    public Optional<SkillVerificationRule> find(SkillShortInfo skill) {
        return rules.stream()
                .filter(r -> r.supports(skill))
                .findFirst();
    }
}

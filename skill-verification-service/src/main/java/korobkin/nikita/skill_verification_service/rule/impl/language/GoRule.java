package korobkin.nikita.skill_verification_service.rule.impl.language;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoRule extends LanguageRule {

    public GoRule() {
        super("Go", List.of(
                "go.mod"
        ));
    }
}
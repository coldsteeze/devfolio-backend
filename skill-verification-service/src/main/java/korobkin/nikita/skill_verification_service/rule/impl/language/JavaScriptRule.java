package korobkin.nikita.skill_verification_service.rule.impl.language;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JavaScriptRule extends LanguageRule {

    public JavaScriptRule() {
        super("JavaScript", List.of(
                "package.json"
        ));
    }
}

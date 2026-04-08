package korobkin.nikita.skill_verification_service.rule.impl.language;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TypeScriptRule extends LanguageRule {

    public TypeScriptRule() {
        super("TypeScript", List.of(
                "tsconfig.json"
        ));
    }
}

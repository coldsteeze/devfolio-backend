package korobkin.nikita.skill_verification_service.rule.impl.language;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JavaRule extends LanguageRule {

    public JavaRule() {
        super("Java", List.of(
                "pom.xml",
                "build.gradle"
        ));
    }
}

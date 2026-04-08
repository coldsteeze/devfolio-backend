package korobkin.nikita.skill_verification_service.rule.impl.framework.java;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringRule extends FileContentRule {

    public SpringRule() {
        super(
                "Spring",
                List.of("pom.xml", "build.gradle"),
                List.of("org.springframework")
        );
    }
}
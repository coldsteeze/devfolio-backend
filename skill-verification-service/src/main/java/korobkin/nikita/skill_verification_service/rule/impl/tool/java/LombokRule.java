package korobkin.nikita.skill_verification_service.rule.impl.tool.java;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LombokRule extends FileContentRule {

    public LombokRule() {
        super(
                "Lombok",
                List.of("pom.xml", "build.gradle"),
                List.of("lombok")
        );
    }
}

package korobkin.nikita.skill_verification_service.rule.impl.framework.java;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringBootRule extends FileContentRule {

    public SpringBootRule() {
        super(
                "Spring Boot",
                List.of("pom.xml", "build.gradle"),
                List.of("spring-boot")
        );
    }
}

package korobkin.nikita.skill_verification_service.rule.impl.tool.java;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestingRule extends FileContentRule {

    public TestingRule() {
        super(
                "Testing",
                List.of("pom.xml", "build.gradle"),
                List.of("junit", "testcontainers", "mockito")
        );
    }
}

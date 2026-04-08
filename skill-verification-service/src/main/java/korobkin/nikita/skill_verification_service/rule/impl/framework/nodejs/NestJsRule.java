package korobkin.nikita.skill_verification_service.rule.impl.framework.nodejs;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NestJsRule extends FileContentRule {

    public NestJsRule() {
        super(
                "NestJS",
                List.of("package.json"),
                List.of("@nestjs/core", "@nestjs/common")
        );
    }
}

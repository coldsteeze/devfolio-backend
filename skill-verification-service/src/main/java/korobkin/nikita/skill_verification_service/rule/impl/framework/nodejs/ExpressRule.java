package korobkin.nikita.skill_verification_service.rule.impl.framework.nodejs;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpressRule extends FileContentRule {

    public ExpressRule() {
        super(
                "Express",
                List.of("package.json"),
                List.of("\"express\"")
        );
    }
}

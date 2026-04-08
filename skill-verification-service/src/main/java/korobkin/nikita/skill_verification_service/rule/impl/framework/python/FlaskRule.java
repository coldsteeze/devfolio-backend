package korobkin.nikita.skill_verification_service.rule.impl.framework.python;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlaskRule extends FileContentRule {

    public FlaskRule() {
        super(
                "Flask",
                List.of("requirements.txt"),
                List.of("flask")
        );
    }
}

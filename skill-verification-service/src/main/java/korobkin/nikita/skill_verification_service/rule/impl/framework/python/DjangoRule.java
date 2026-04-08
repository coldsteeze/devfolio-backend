package korobkin.nikita.skill_verification_service.rule.impl.framework.python;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DjangoRule extends FileContentRule {

    public DjangoRule() {
        super(
                "Django",
                List.of("requirements.txt", "pyproject.toml"),
                List.of("django")
        );
    }
}

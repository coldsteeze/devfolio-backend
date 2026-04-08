package korobkin.nikita.skill_verification_service.rule.impl.language;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PythonRule extends LanguageRule {

    public PythonRule() {
        super("Python", List.of(
                "requirements.txt",
                "pyproject.toml",
                "setup.py"
        ));
    }
}

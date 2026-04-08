package korobkin.nikita.skill_verification_service.rule.impl.tool;

import korobkin.nikita.skill_verification_service.rule.CommonFiles;
import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MySqlRule extends FileContentRule {

    public MySqlRule() {
        super(
                "MySQL",
                CommonFiles.BACKEND,
                List.of(
                        "mysql",
                        "jdbc:mysql",
                        "mysql://",
                        "spring.datasource.url=jdbc:mysql",
                        "mysql2" // node.js
                )
        );
    }
}

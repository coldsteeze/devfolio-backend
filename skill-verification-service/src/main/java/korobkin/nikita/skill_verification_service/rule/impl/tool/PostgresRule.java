package korobkin.nikita.skill_verification_service.rule.impl.tool;

import korobkin.nikita.skill_verification_service.rule.CommonFiles;
import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostgresRule extends FileContentRule {

    public PostgresRule() {
        super(
                "PostgreSQL",
                CommonFiles.BACKEND,
                List.of(
                        "postgres",
                        "postgresql",
                        "jdbc:postgresql",
                        "postgres://",
                        "spring.datasource.url=jdbc:postgresql"
                )
        );
    }
}
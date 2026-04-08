package korobkin.nikita.skill_verification_service.rule.impl.framework.java;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HibernateRule extends FileContentRule {

    public HibernateRule() {
        super(
                "Hibernate",
                List.of("pom.xml"),
                List.of("hibernate", "spring-data-jpa", "jakarta.persistence")
        );
    }
}

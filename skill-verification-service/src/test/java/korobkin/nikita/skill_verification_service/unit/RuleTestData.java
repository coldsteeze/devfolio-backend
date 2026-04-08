package korobkin.nikita.skill_verification_service.unit;

import korobkin.nikita.skill_verification_service.rule.impl.framework.java.HibernateRule;
import korobkin.nikita.skill_verification_service.rule.impl.framework.java.SpringBootRule;
import korobkin.nikita.skill_verification_service.rule.impl.framework.java.SpringRule;
import korobkin.nikita.skill_verification_service.rule.impl.framework.java.SpringSecurityRule;
import korobkin.nikita.skill_verification_service.rule.impl.framework.nodejs.ExpressRule;
import korobkin.nikita.skill_verification_service.rule.impl.framework.nodejs.NestJsRule;
import korobkin.nikita.skill_verification_service.rule.impl.framework.python.DjangoRule;
import korobkin.nikita.skill_verification_service.rule.impl.framework.python.FlaskRule;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class RuleTestData {

    static Stream<Arguments> provideFileContentRules() {
        return Stream.of(
                Arguments.of(new HibernateRule(), "pom.xml", "hibernate"),
                Arguments.of(new SpringBootRule(), "pom.xml", "spring-boot"),
                Arguments.of(new SpringRule(), "pom.xml", "org.springframework"),
                Arguments.of(new SpringSecurityRule(), "pom.xml", "spring-boot-starter-security"),
                Arguments.of(new ExpressRule(), "package.json", "\"express\""),
                Arguments.of(new NestJsRule(), "package.json", "@nestjs/core"),
                Arguments.of(new DjangoRule(), "requirements.txt", "django"),
                Arguments.of(new FlaskRule(), "requirements.txt", "flask")
        );
    }
}

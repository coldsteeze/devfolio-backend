package korobkin.nikita.skill_verification_service.rule.impl.platform;

import korobkin.nikita.skill_verification_service.rule.FileContentRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaRule extends FileContentRule {

    public KafkaRule() {
        super(
                "Kafka",
                List.of("pom.xml", "build.gradle", "requirements.txt", "package.json"),
                List.of("kafka", "spring-kafka", "kafkajs", "confluent-kafka")
        );
    }
}

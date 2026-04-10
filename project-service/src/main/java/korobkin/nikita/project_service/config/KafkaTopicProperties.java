package korobkin.nikita.project_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
@Getter
@Setter
public class KafkaTopicProperties {

    private String projectSkillVerificationRequested;
    private String projectSkillVerificationCompleted;
    private String projectCreated;
    private String projectUpdated;
    private String projectDeleted;
    private String projectSkillAdded;
}

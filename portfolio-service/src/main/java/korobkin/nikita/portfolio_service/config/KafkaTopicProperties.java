package korobkin.nikita.portfolio_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
@Getter
@Setter
public class KafkaTopicProperties {

    private String userDeleted;
    private String userProfileUpdated;
    private String userProfileAvatarUpdated;
    private String projectCreated;
    private String projectUpdated;
    private String projectDeleted;
    private String projectSkillAdded;
    private String projectSkillRemoved;
    private String projectSkillsUpdated;
    private String projectPreviewUpdated;
}

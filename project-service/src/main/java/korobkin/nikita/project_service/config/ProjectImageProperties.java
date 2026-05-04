package korobkin.nikita.project_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "project.images")
@Getter
@Setter
public class ProjectImageProperties {

    private int maxCount;
}

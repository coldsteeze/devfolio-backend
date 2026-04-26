package korobkin.nikita.media_service.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "media")
public class MediaProperties {

    @NotNull(message = "max-file-size must be configured")
    private DataSize maxFileSize;

    @NotEmpty(message = "allowed-types must not be empty")
    private List<@NotEmpty String> allowedTypes;
}

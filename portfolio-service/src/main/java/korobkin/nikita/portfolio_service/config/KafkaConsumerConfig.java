package korobkin.nikita.portfolio_service.config;

import korobkin.nikita.events.*;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz) {

        JsonDeserializer<T> deserializer =
                new JsonDeserializer<>(clazz);

        deserializer.addTrustedPackages("korobkin.nikita.events");

        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(null),
                new StringDeserializer(),
                deserializer
        );
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T>
    kafkaListenerContainerFactory(Class<T> clazz) {

        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory(clazz));

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent>
    userDeletedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(UserDeletedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserProfileUpdatedEvent>
    userProfileUpdatedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(UserProfileUpdatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserProfileAvatarUpdatedEvent>
    userProfileAvatarUpdatedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(UserProfileAvatarUpdatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectCreatedEvent>
    projectCreatedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(ProjectCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectDeletedEvent>
    projectDeletedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(ProjectDeletedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectUpdatedEvent>
    projectUpdatedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(ProjectUpdatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectSkillAddedEvent>
    projectSkillAddedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(ProjectSkillAddedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectSkillRemovedEvent>
    projectSkillRemovedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(ProjectSkillRemovedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectSkillsUpdatedEvent>
    projectSkillsUpdatedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(ProjectSkillsUpdatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProjectPreviewUpdatedEvent>
    projectPreviewUpdatedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(ProjectPreviewUpdatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserProfileCareerUpdatedEvent>
    userProfileCareerUpdatedKafkaListenerContainerFactory() {

        return kafkaListenerContainerFactory(UserProfileCareerUpdatedEvent.class);
    }
}
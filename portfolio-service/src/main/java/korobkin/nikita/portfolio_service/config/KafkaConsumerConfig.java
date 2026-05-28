package korobkin.nikita.portfolio_service.config;

import korobkin.nikita.events.UserDeletedEvent;
import korobkin.nikita.events.UserProfileAvatarUpdatedEvent;
import korobkin.nikita.events.UserProfileUpdatedEvent;
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
}
package korobkin.nikita.portfolio_service.kafka.consumer.user;

import korobkin.nikita.events.UserProfileAvatarUpdatedEvent;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileAvatarUpdatedListener {

    private final PortfolioService portfolioService;
    private final ProcessedEventService processedEventService;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userProfileAvatarUpdated}",
            containerFactory = "userProfileAvatarUpdatedKafkaListenerContainerFactory"
    )
    public void handleUserProfileUpdated(UserProfileAvatarUpdatedEvent event) {
        log.info("received UserProfileAvatarUpdatedEvent: {}", event);

        processedEventService.process(
                event.eventId(),
                () -> portfolioService.updatePortfolioAvatar(event)
        );
    }
}

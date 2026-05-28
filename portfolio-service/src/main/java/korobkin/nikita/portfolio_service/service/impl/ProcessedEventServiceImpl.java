package korobkin.nikita.portfolio_service.service.impl;

import korobkin.nikita.portfolio_service.entity.ProcessedEvent;
import korobkin.nikita.portfolio_service.repository.ProcessedEventRepository;
import korobkin.nikita.portfolio_service.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessedEventServiceImpl implements ProcessedEventService {

    private final ProcessedEventRepository processedEventRepository;

    @Override
    @Transactional
    public void process(UUID eventId, Runnable action) {
        if (processedEventRepository.existsById(eventId)) {

            log.info(
                    "event already processed: {}",
                    eventId
            );

            return;
        }

        action.run();

        ProcessedEvent processedEvent =
                ProcessedEvent.builder()
                        .eventId(eventId)
                        .processedAt(Instant.now())
                        .build();

        processedEventRepository.save(processedEvent);

        log.info(
                "event processed successfully: {}",
                eventId
        );
    }
}

package korobkin.nikita.portfolio_service.service;

import java.util.UUID;

public interface ProcessedEventService {

    void process(UUID eventId, Runnable action);
}

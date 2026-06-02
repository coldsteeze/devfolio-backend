package korobkin.nikita.user_profile_service.repository;

import korobkin.nikita.user_profile_service.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}

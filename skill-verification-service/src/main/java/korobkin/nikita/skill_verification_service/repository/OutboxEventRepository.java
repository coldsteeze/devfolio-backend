package korobkin.nikita.skill_verification_service.repository;

import korobkin.nikita.skill_verification_service.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
}

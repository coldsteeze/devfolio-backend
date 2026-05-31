package korobkin.nikita.skill_verification_service.repository;

import korobkin.nikita.skill_verification_service.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
}

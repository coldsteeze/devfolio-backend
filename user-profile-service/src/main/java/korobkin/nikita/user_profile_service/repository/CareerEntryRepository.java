package korobkin.nikita.user_profile_service.repository;

import korobkin.nikita.user_profile_service.entity.CareerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CareerEntryRepository extends JpaRepository<CareerEntry, UUID> {

    List<CareerEntry> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}

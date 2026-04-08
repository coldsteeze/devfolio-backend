package korobkin.nikita.skill_verification_service.repository;

import korobkin.nikita.skill_verification_service.entity.VerifiedSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerifiedSkillRepository extends JpaRepository<VerifiedSkill, UUID> {

    void deleteByProjectId(UUID projectId);
}

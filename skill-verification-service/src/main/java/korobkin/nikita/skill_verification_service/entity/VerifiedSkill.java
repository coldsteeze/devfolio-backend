package korobkin.nikita.skill_verification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verified_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifiedSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "project_skill_id", nullable = false, updatable = false)
    private UUID projectSkillId;

    @Column(name = "skill_id", nullable = false, updatable = false)
    private UUID skillId;

    @Column(name = "skill_name", nullable = false, updatable = false)
    private String skillName;

    @CreationTimestamp
    @Column(name = "verified_at", nullable = false, updatable = false)
    private Instant verifiedAt;
}

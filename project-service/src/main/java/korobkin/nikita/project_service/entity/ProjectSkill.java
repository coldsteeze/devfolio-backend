package korobkin.nikita.project_service.entity;

import jakarta.persistence.*;
import korobkin.nikita.project_service.entity.enums.SkillCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "project_skills",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"project_id", "skill_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "skill_id", nullable = false)
    private UUID skillId;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "skill_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillCategory skillCategory;

    @Column(name = "manually_added", nullable = false)
    private boolean manuallyAdded;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed;
}

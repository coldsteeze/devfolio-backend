package korobkin.nikita.portfolio_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "portfolio_projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioProject {

    @Id
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "github_url", nullable = false)
    private String githubUrl;

    @Column(name = "project_public")
    private boolean projectPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ElementCollection
    @CollectionTable(
            name = "portfolio_project_skills",
            joinColumns = @JoinColumn(name = "portfolio_project_id")
    )
    private Set<PortfolioProjectSkill> skills = new HashSet<>();
}

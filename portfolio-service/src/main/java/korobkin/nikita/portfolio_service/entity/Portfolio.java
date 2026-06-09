package korobkin.nikita.portfolio_service.entity;

import jakarta.persistence.*;
import korobkin.nikita.events.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "portfolios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {

    @Id
    private UUID userId;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "bio")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column(name = "total_projects")
    private short totalProjects;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioProject> projects = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioCareerEntry> careerEntries = new ArrayList<>();

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void addProject(PortfolioProject project) {
        if (project == null) return;

        if (containsProject(project.getProjectId())) {
            return;
        }

        projects.add(project);
        project.setPortfolio(this);

        recalcProjects();
    }

    public void removeProject(UUID projectId) {
        boolean removed = projects.removeIf(p -> p.getProjectId().equals(projectId));

        if (!removed) {
            return;
        }

        recalcProjects();
    }

    private boolean containsProject(UUID projectId) {
        return projects.stream()
                .anyMatch(p -> p.getProjectId().equals(projectId));
    }

    private void recalcProjects() {
        this.totalProjects = (short) projects.size();
    }
}

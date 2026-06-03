package korobkin.nikita.portfolio_service.entity;

import jakarta.persistence.*;
import korobkin.nikita.events.ProjectSkillDto;
import lombok.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "description")
    private String description;

    @Column(name = "github_url", nullable = false)
    private String githubUrl;

    @Column(name = "project_public")
    private boolean projectPublic;

    @Column(name = "main_image_url")
    private String mainImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ElementCollection
    @CollectionTable(
            name = "portfolio_project_skills",
            joinColumns = @JoinColumn(name = "portfolio_project_id")
    )
    private Set<PortfolioProjectSkill> skills = new HashSet<>();

    public void addSkill(PortfolioProjectSkill skill) {
        if (skill == null) return;
        skills.add(skill);
    }

    public void removeSkill(String skillName) {
        if (skillName == null) return;

        skills.removeIf(s -> s.getSkillName().equals(skillName));
    }

    public void updateSkills(List<ProjectSkillDto> dtos) {
        if (dtos == null) return;

        Map<String, PortfolioProjectSkill> map = skills.stream()
                .collect(Collectors.toMap(
                        PortfolioProjectSkill::getSkillName,
                        Function.identity()
                ));

        for (ProjectSkillDto dto : dtos) {
            PortfolioProjectSkill skill = map.get(dto.skillName());

            if (skill != null) {
                skill.confirm(dto.confirmed());
            }
        }
    }
}

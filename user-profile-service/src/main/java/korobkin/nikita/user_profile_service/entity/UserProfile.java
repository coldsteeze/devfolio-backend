package korobkin.nikita.user_profile_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "user_profiles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfile {

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false, updatable = false)
    @Id
    private UUID userId;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @ElementCollection
    @CollectionTable(
            name = "profile_skills",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "skill", nullable = false)
    private Set<String> skills = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "profile_links",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @MapKeyColumn(name = "link_type", length = 20)
    @Column(name = "url", length = 500)
    private Map<String, String> links = new HashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

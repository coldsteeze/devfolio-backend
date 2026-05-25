package korobkin.nikita.user_profile_service.entity;

import jakarta.persistence.*;
import korobkin.nikita.user_profile_service.entity.enums.UserType;
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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "bio")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType = UserType.JOB_SEEKER;

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

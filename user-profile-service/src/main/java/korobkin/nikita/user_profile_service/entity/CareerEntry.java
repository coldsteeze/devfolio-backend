package korobkin.nikita.user_profile_service.entity;

import jakarta.persistence.*;
import korobkin.nikita.user_profile_service.entity.enums.CareerEntryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "career_entries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CareerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CareerEntryType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "organization", nullable = false)
    private String organization;

    @Column(name = "description")
    private String description;

    @Column(name = "start_month", nullable = false)
    private Integer startMonth;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "end_year")
    private Integer endYear;
}

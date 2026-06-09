package korobkin.nikita.portfolio_service.entity;

import jakarta.persistence.*;
import korobkin.nikita.portfolio_service.entity.enums.CareerEntryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "portfolio_career_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCareerEntry {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Portfolio portfolio;

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

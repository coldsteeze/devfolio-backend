package korobkin.nikita.portfolio_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.portfolio_service.entity.enums.CareerEntryType;

import java.util.UUID;

public record PortfolioCareerEntryResponse(

        @Schema(example = "550e8400-e29b-41d4-a716-446655440000", description = "Career entry ID")
        UUID id,

        @Schema(example = "WORK", description = "Type of career entry")
        CareerEntryType type,

        @Schema(example = "Java Developer", description = "Title")
        String title,

        @Schema(example = "EPAM Systems", description = "Organization")
        String organization,

        @Schema(example = "Worked on microservices backend", description = "Description")
        String description,

        @Schema(description = "Start date")
        PortfolioCareerDateResponse startDate,

        @Schema(description = "End date (null if current job)")
        PortfolioCareerDateResponse endDate
) {
}

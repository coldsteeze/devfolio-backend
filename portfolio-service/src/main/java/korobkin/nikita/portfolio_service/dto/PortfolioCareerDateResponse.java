package korobkin.nikita.portfolio_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PortfolioCareerDateResponse(

        @Schema(example = "6", description = "Month (1-12)")
        Integer month,

        @Schema(example = "2024", description = "Year")
        Integer year
) {
}

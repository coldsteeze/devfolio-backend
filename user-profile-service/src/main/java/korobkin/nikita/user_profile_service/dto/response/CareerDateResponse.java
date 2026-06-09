package korobkin.nikita.user_profile_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Career date response")
public record CareerDateResponse(

        @Schema(example = "6", description = "Month (1-12)")
        Integer month,

        @Schema(example = "2024", description = "Year")
        Integer year
) {}

package korobkin.nikita.user_profile_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import korobkin.nikita.user_profile_service.entity.enums.CareerEntryType;

import java.util.UUID;

@Schema(description = "Career entry response")
public record CareerEntryResponse(

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
        CareerDateResponse startDate,

        @Schema(description = "End date (null if current job)")
        CareerDateResponse endDate
) {}

package korobkin.nikita.user_profile_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Career response containing list of career entries")
public record CareerResponse(

        @Schema(description = "List of career entries")
        List<CareerEntryResponse> items
) {}

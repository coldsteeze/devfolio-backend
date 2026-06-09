package korobkin.nikita.user_profile_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request to update full career (replaces existing entries)")
public record UpdateCareerRequest(

        @NotNull(message = "Career items list cannot be null")
        @Schema(description = "List of career entries")
        List<@Valid CareerEntryRequest> items
) {}

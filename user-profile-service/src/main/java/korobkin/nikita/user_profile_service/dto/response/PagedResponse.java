package korobkin.nikita.user_profile_service.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paged response for any type of content")
public record PagedResponse<T>(

        @Schema(description = "List of items for the current page")
        @ArraySchema(
                arraySchema = @Schema(description = "Array of items"),
                schema = @Schema(implementation = UserProfileResponse.class)
        )
        List<T> content,

        @Schema(description = "Current page number (0-based)", example = "0")
        int pageNumber,

        @Schema(description = "Number of items per page", example = "5")
        int pageSize,

        @Schema(description = "Total number of elements across all pages", example = "42")
        long totalElements,

        @Schema(description = "Total number of pages available", example = "9")
        int totalPages
) {
}


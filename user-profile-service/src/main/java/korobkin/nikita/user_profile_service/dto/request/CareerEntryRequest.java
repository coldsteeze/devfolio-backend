package korobkin.nikita.user_profile_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import korobkin.nikita.user_profile_service.entity.enums.CareerEntryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Career entry (work or education item)")
public class CareerEntryRequest {

    @NotNull(message = "Career entry type is required")
    @Schema(example = "WORK", description = "Type of career entry (WORK or EDUCATION)")
    private CareerEntryType type;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    @Schema(example = "Java Developer", description = "Position or education title")
    private String title;

    @NotBlank(message = "Organization is required")
    @Size(max = 150, message = "Organization must be at most 150 characters")
    @Schema(example = "EPAM Systems", description = "Company or institution name")
    private String organization;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    @Schema(example = "Worked on backend microservices...", description = "Optional description")
    private String description;

    @NotNull(message = "Start date is required")
    @Valid
    @Schema(description = "Start date of career entry")
    private CareerDateRequest startDate;

    @Valid
    @Schema(description = "End date of career entry (null if current)")
    private CareerDateRequest endDate;
}

package korobkin.nikita.project_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Request filter DTO for searching user's projects")
public class ProjectFilterRequest {

    @Schema(example = "portfolio", description = "Partial search in project name")
    private String search;

    @Schema(description = "Filter by project visibility (true = only public projects, false = only private projects)")
    private Boolean projectPublic;

    @Schema(description = "Filter by creation date (start, optional)")
    private LocalDateTime createdAfter;

    @Schema(description = "Filter by creation date (end, optional)")
    private LocalDateTime createdBefore;
}
package korobkin.nikita.project_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.project_service.dto.request.ProjectFilterRequest;
import korobkin.nikita.project_service.dto.response.PagedResponse;
import korobkin.nikita.project_service.dto.response.ProjectFavoriteResponse;
import korobkin.nikita.project_service.dto.response.ProjectResponse;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "User Project", description = "Get user projects")
public interface UserProjectControllerDocs {

    @Operation(
            summary = "List projects with filters",
            description = "Fetch paginated projects with filters",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Projects fetched successfully"
                    )
            }
    )
    ResponseEntity<PagedResponse<ProjectResponse>> getUserProjects(
            @PathVariable UUID userId,
            UserPrincipal principal,
            @ParameterObject ProjectFilterRequest request,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "List project favorites with filters",
            description = "Fetch paginated favorite projects with filters",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project favorites fetched successfully"
                    )
            }
    )
    ResponseEntity<PagedResponse<ProjectFavoriteResponse>> getUserProjectFavorites(
            UserPrincipal principal,
            @ParameterObject Pageable pageable
    );
}

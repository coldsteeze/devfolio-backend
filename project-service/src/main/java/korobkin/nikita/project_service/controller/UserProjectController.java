package korobkin.nikita.project_service.controller;

import korobkin.nikita.project_service.dto.request.ProjectFilterRequest;
import korobkin.nikita.project_service.dto.response.PagedResponse;
import korobkin.nikita.project_service.dto.response.ProjectResponse;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import korobkin.nikita.project_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserProjectController {

    private final ProjectService projectService;

    @GetMapping("/{userId}/projects")
    public ResponseEntity<PagedResponse<ProjectResponse>> getUserProjects(
            @PathVariable("userId") UUID userId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            ProjectFilterRequest request,
            Pageable pageable) {
        return ResponseEntity.ok(projectService.getUserProjects(userId, currentUser, request, pageable));
    }
}

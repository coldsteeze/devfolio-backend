package korobkin.nikita.project_service.controller;

import jakarta.validation.Valid;
import korobkin.nikita.project_service.docs.ProjectControllerDocs;
import korobkin.nikita.project_service.dto.request.CreateProjectRequest;
import korobkin.nikita.project_service.dto.request.UpdateProjectRequest;
import korobkin.nikita.project_service.dto.response.*;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import korobkin.nikita.project_service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController implements ProjectControllerDocs {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(request, currentUser));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @Valid @RequestBody UpdateProjectRequest request,
            @PathVariable("projectId") UUID projectId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(projectService.updateProject(request, projectId, currentUser));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailsResponse> getProject(
            @PathVariable("projectId") UUID projectId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(projectService.getProject(projectId, currentUser));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable("projectId") UUID projectId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        projectService.deleteProject(projectId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/skills/{skillId}")
    public ResponseEntity<ProjectSkillResponse> addSkillProject(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("skillId") UUID skillId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(projectService.addSkillProject(projectId, skillId, currentUser));
    }

    @DeleteMapping("/{projectId}/skills/{skillId}")
    public ResponseEntity<Void> deleteSkillProject(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("skillId") UUID skillId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        projectService.deleteSkillProject(projectId, skillId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/verifications")
    public ResponseEntity<VerificationResponse> verifySkillProject(
            @PathVariable("projectId") UUID projectId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(projectService.verifySkillProject(projectId, currentUser));
    }

    @GetMapping("/{projectId}/skills")
    public ResponseEntity<List<ProjectSkillResponse>> getProjectSkills(
        @PathVariable("projectId") UUID projectId,
        @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(projectService.getProjectSkills(projectId, currentUser));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProjectFeedResponse>> getProjectsFeed(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(projectService.getProjectsFeed(pageable));
    }
}

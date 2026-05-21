package korobkin.nikita.project_service.controller;

import korobkin.nikita.project_service.docs.ProjectInteractionControllerDocs;
import korobkin.nikita.project_service.dto.response.LikeStatusResponse;
import korobkin.nikita.project_service.security.user.UserPrincipal;
import korobkin.nikita.project_service.service.ProjectInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}")
public class ProjectInteractionController implements ProjectInteractionControllerDocs {

    private final ProjectInteractionService projectInteractionService;

    @PostMapping("/view")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recordView(
            @PathVariable("projectId") UUID projectId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        projectInteractionService.recordView(projectId, currentUser);
    }

    @PostMapping("/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void like(
            @PathVariable("projectId") UUID projectId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        projectInteractionService.like(projectId, currentUser);
    }

    @DeleteMapping("/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(
            @PathVariable("projectId") UUID projectId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        projectInteractionService.removeLike(projectId, currentUser);
    }

    @GetMapping("/like/status")
    public ResponseEntity<LikeStatusResponse> getLikeStatus(
            @PathVariable("projectId") UUID projectId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(projectInteractionService.isLiked(projectId, currentUser));
    }
}

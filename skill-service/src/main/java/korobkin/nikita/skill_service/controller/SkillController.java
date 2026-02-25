package korobkin.nikita.skill_service.controller;

import jakarta.validation.Valid;
import korobkin.nikita.skill_service.docs.SkillControllerDocs;
import korobkin.nikita.skill_service.dto.request.BulkSkillRequest;
import korobkin.nikita.skill_service.dto.request.CreateSkillRequest;
import korobkin.nikita.skill_service.dto.request.SkillFilterRequest;
import korobkin.nikita.skill_service.dto.request.UpdateSkillRequest;
import korobkin.nikita.skill_service.dto.response.PagedResponse;
import korobkin.nikita.skill_service.dto.response.SkillResponse;
import korobkin.nikita.skill_service.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/skills")
public class SkillController implements SkillControllerDocs {

    private final SkillService skillService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PagedResponse<SkillResponse>> getSkills(
            SkillFilterRequest skillFilterRequest,
            Pageable pageable) {
        return ResponseEntity.ok(skillService.findSkills(skillFilterRequest, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SkillResponse> getSkill(@PathVariable("id") UUID skillId) {
        return ResponseEntity.ok(skillService.findSkill(skillId));
    }

    @PostMapping("/by-ids")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<SkillResponse>> getBulkSkills(@Valid @RequestBody BulkSkillRequest request) {
        return ResponseEntity.ok(skillService.findBulkSkills(request));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponse> createSkill(
            @Valid @RequestBody CreateSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(skillService.createSkill(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable("id") UUID skillId,
            @Valid @RequestBody UpdateSkillRequest request) {
        return ResponseEntity.ok(skillService.updateSkill(skillId, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateSkill(
            @PathVariable("id") UUID skillId) {
        skillService.deactivateSkill(skillId);
        return ResponseEntity.noContent().build();
    }
}

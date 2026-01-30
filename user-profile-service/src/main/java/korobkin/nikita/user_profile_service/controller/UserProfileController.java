package korobkin.nikita.user_profile_service.controller;

import jakarta.validation.Valid;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileAvatarRequest;
import korobkin.nikita.user_profile_service.dto.request.UpdateUserProfileRequest;
import korobkin.nikita.user_profile_service.dto.response.UserProfileResponse;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import korobkin.nikita.user_profile_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userProfileService.getUserProfile(principal.userId()));
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> fillMyProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userProfileService.fillUserProfile(principal.userId(), request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(principal.userId(), request));
    }

    @PatchMapping("/me/avatar")
    public ResponseEntity<UserProfileResponse> updateProfileAvatar(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserProfileAvatarRequest request) {
        return ResponseEntity.ok(userProfileService.updateUserProfileAvatar(principal.userId(), request));
    }

    @GetMapping
    public ResponseEntity<Page<UserProfileResponse>> searchProfilesBySkills(
            @RequestParam("skills") Set<String> skills,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userProfileService.findBySkills(skills, pageable));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        userProfileService.deleteUserProfile(principal.userId());
        return ResponseEntity.noContent().build();
    }
}

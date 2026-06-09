package korobkin.nikita.user_profile_service.controller;

import jakarta.validation.Valid;
import korobkin.nikita.user_profile_service.docs.CareerControllerDocs;
import korobkin.nikita.user_profile_service.dto.request.UpdateCareerRequest;
import korobkin.nikita.user_profile_service.dto.response.CareerResponse;
import korobkin.nikita.user_profile_service.security.user.UserPrincipal;
import korobkin.nikita.user_profile_service.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class CareerController implements CareerControllerDocs {

    private final CareerService careerService;

    @GetMapping("/{userId}/career")
    public ResponseEntity<CareerResponse> getCareer(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(careerService.getCareer(userId));
    }

    @PutMapping("/me/career")
    public ResponseEntity<CareerResponse> updateCareer(
            @Valid @RequestBody UpdateCareerRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(careerService.updateCareer(principal.userId(), request));
    }
}

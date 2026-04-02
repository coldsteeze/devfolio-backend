package korobkin.nikita.project_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO representing verification result")
public record VerificationResponse(

        @Schema(example = "VERIFICATION_REQUESTED", description = "Verification status")
        String status
) {
}

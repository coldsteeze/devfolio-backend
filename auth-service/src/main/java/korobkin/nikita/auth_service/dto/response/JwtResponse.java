package korobkin.nikita.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response containing access and refresh tokens")
public record JwtResponse(

        @Schema(description = "Access token for authenticated requests",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Minute expiration time of the access token",
                example = "15")
        long accessTokenExpiresIn
) { }

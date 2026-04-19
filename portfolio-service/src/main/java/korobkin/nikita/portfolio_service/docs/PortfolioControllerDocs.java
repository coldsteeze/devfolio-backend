package korobkin.nikita.portfolio_service.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import korobkin.nikita.portfolio_service.dto.PortfolioResponse;
import korobkin.nikita.portfolio_service.exception.ApiError;
import korobkin.nikita.portfolio_service.security.user.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Portfolio", description = "Get portfolios")
public interface PortfolioControllerDocs {

    @Operation(
            summary = "Get portfolio",
            description = "Get information about user portfolio",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Portfolio fetched successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PortfolioResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Portfolio not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    ResponseEntity<PortfolioResponse> getPortfolio(
            @PathVariable UUID userId
    );


    @Operation(
            summary = "Get my portfolio",
            description = "Get information about my portfolio",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Portfolio fetched successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PortfolioResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<PortfolioResponse> getMyPortfolio(
            UserPrincipal userPrincipal
    );
}

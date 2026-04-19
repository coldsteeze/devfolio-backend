package korobkin.nikita.portfolio_service.controller;

import korobkin.nikita.portfolio_service.docs.PortfolioControllerDocs;
import korobkin.nikita.portfolio_service.dto.PortfolioResponse;
import korobkin.nikita.portfolio_service.security.user.UserPrincipal;
import korobkin.nikita.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController implements PortfolioControllerDocs {

    private final PortfolioService portfolioService;

    @GetMapping("/{userId}")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(portfolioService.getPortfolio(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<PortfolioResponse> getMyPortfolio(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(portfolioService.getMyPortfolio(currentUser));
    }
}

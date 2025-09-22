package nikita.korobkin.gateway_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final UriProperties uriProperties;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service",
                        r -> r.path("/api/auth/**").uri(uriProperties.getAuthServiceUri()))
                .route("user-profile-service",
                        r -> r.path("/api/profiles/**").uri(uriProperties.getUserProfileServiceUri()))
                .build();
    }
}

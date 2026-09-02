package net.modistry.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    @Bean
    RouteLocator gateway(RouteLocatorBuilder rlb) {
        return rlb.routes()
                .route(rs -> rs
                        .path("/")
                        .filters(GatewayFilterSpec::tokenRelay)
                        .uri("http://localhost:8080")
                )
                .route("modistry-api", rs -> rs
                        .path("/api/**")
                        .filters(filters -> filters.stripPrefix(1).tokenRelay())
                        .uri("http://localhost:8080")
                )
                .build();
    }
}

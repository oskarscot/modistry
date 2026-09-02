package net.modistry.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class RouteConfiguration {

    @Bean
    RouterFunction<ServerResponse> rootRoute() {
        return route("modistry-root")
                .route(path("/"), http())
                .before(uri("http://localhost:8080"))
                .filter(tokenRelay())
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> apiRoute() {
        return route("modistry-api")
                .route(path("/api/**"), http())
                .before(uri("http://localhost:8080"))
                .before(stripPrefix(1))
                .filter(tokenRelay())
                .build();
    }
}

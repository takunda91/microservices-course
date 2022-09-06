package com.guru.springframewrok.beergateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local-discovery")
public class LoadBalancedRoutes {

    @Bean
    public RouteLocator loadBalancedRoutesBean(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(r -> r.path("/api/v1/beer*", "/api/v1/beer/*","/api/v1/beerUpc/*")
                        .uri("lb://beer-service")
                )
                .route(r -> r.path("/api/v1/customers/**")
                        .uri("lb://beer-order-service"))
                .route(r -> r.path("/api/v1/beer/*/inventory")
                        .filters(f -> f.circuitBreaker(c -> c.setName("inventoryCB")
                                .setFallbackUri("forward:/inventory-failover")
                                .setRouteId("inv-failover")
                        ))
                        .uri("lb://beer-inventory-service")
                )
                .route(r -> r.path("/inventory-failover/**")
                        .uri("lb://beer-inventory-failover-service"))
                .build();
    }



}

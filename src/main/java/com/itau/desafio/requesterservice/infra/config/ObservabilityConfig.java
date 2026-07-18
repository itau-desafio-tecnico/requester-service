package com.itau.desafio.requesterservice.infra.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

@Configuration
public class ObservabilityConfig {

    @Bean
    public ObservationPredicate excludeHealthCheckObservations() {
        return (name, context) -> {
            if (context instanceof ServerRequestObservationContext serverContext) {
                String uri = serverContext.getCarrier().getRequestURI();
                return !uri.endsWith("/actuator/health") && !uri.endsWith("/actuator/prometheus");
            }
            return true;
        };
    }
}

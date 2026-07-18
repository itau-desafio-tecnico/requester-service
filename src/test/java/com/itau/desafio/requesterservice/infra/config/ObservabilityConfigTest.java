package com.itau.desafio.requesterservice.infra.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationPredicate;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.observation.ServerRequestObservationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservabilityConfigTest {

    @Mock
    private HttpServletRequest request;

    private ObservationPredicate predicate;

    @BeforeEach
    void setUp() {
        predicate = new ObservabilityConfig().excludeHealthCheckObservations();
    }

    @Test
    void shouldExcludeHealthCheckObservations() {
        when(request.getRequestURI()).thenReturn("/jv-requester-service/actuator/health");
        ServerRequestObservationContext context = new ServerRequestObservationContext(request, null);

        assertThat(predicate.test("http.server.requests", context)).isFalse();
    }

    @Test
    void shouldExcludePrometheusObservations() {
        when(request.getRequestURI()).thenReturn("/jv-requester-service/actuator/prometheus");
        ServerRequestObservationContext context = new ServerRequestObservationContext(request, null);

        assertThat(predicate.test("http.server.requests", context)).isFalse();
    }

    @Test
    void shouldKeepObservationsForOtherEndpoints() {
        when(request.getRequestURI()).thenReturn("/jv-requester-service/requesters");
        ServerRequestObservationContext context = new ServerRequestObservationContext(request, null);

        assertThat(predicate.test("http.server.requests", context)).isTrue();
    }

    @Test
    void shouldKeepObservationsForNonHttpRequestContexts() {
        Observation.Context context = new Observation.Context();

        assertThat(predicate.test("qualquer.observacao", context)).isTrue();
    }
}

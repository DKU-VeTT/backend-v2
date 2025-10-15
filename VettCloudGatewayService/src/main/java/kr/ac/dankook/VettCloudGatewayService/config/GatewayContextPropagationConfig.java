package kr.ac.dankook.VettCloudGatewayService.config;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.integration.Slf4jThreadLocalAccessor;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.contextpropagation.ObservationAwareBaggageThreadLocalAccessor;
import io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
@RequiredArgsConstructor
@Slf4j
class GatewayContextPropagationConfig {

    private final Tracer tracer;
    private final ObservationRegistry observationRegistry;

    @PostConstruct
    void init() {
        Hooks.enableAutomaticContextPropagation();
        var reg = ContextRegistry.getInstance();
        reg.registerThreadLocalAccessor(new Slf4jThreadLocalAccessor());
        reg.registerThreadLocalAccessor(new ObservationThreadLocalAccessor());
        reg.registerThreadLocalAccessor(new ObservationAwareSpanThreadLocalAccessor(observationRegistry, tracer));
        reg.registerThreadLocalAccessor(new ObservationAwareBaggageThreadLocalAccessor(observationRegistry, tracer));
    }
}
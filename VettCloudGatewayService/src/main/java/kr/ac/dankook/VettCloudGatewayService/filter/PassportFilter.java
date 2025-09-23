package kr.ac.dankook.VettCloudGatewayService.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettCloudGatewayService.dto.Passport;
import kr.ac.dankook.VettCloudGatewayService.error.CustomException;
import kr.ac.dankook.VettCloudGatewayService.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class PassportFilter implements GlobalFilter {

    @Value("${app.secret.passport}")
    private String SECRET;
    @Value("${app.domain.auth-service}")
    private String AUTH_SERVICE_DOMAIN;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String token = exchange.getAttribute("token");

        return webClientBuilder.build()
                .get()
                .uri(AUTH_SERVICE_DOMAIN)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Passport.class)
                .<String>handle((passport, sink) -> {
                    try {
                        sink.next(objectMapper.writeValueAsString(passport));
                    } catch (JsonProcessingException e) {
                        sink.error(Exceptions.propagate(e));
                    }
                })
                .flatMap(passportPayload -> {
                    String encodedPayload = Base64.getEncoder().encodeToString(passportPayload.getBytes(StandardCharsets.UTF_8));
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .headers(httpHeaders -> {
                                httpHeaders.remove("X-Passport-Secret");
                                httpHeaders.add("X-Passport-Secret", SECRET);
                                httpHeaders.remove("X-Passport");
                                httpHeaders.add("X-Passport", encodedPayload);
                            })
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();
                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(JsonProcessingException.class, ex ->
                        Mono.error(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR))
                )
                .onErrorResume(ex ->
                        Mono.error(new CustomException(ErrorCode.UNAUTHORIZED))
                );
    }
}

package kr.ac.dankook.VettCloudGatewayService.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.Passport;
import kr.ac.dankook.VettCloudGatewayService.dto.PassportResponse;
import kr.ac.dankook.VettCloudGatewayService.error.CustomException;
import kr.ac.dankook.VettCloudGatewayService.error.ErrorCode;
import kr.ac.dankook.VettCloudGatewayService.service.PassportGrpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
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
    private final PassportGrpcService passportGrpcService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/v1/auth") || path.startsWith("/actuator") || path.startsWith("/ws") ||
                path.startsWith("/pub") || path.startsWith("/sub")){
            return chain.filter(exchange);
        }
        String key = exchange.getAttribute("key");
        Passport.PassportResponse passport = passportGrpcService.getPassportInfo(key);
        if (passport == null) throw new CustomException(ErrorCode.PASSPORT_ERROR);

        PassportResponse passportEntity = new PassportResponse(passport);
        String payload;
        try{
            payload = objectMapper.writeValueAsString(passportEntity);
        }catch (JsonProcessingException e){
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }
        String encodedPayload = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove("X-Passport-Secret");
                    httpHeaders.add("X-Passport-Secret", SECRET);
                    httpHeaders.remove("X-Passport");
                    httpHeaders.add("X-Passport", encodedPayload);
                })
                .build();
        log.info(
                "[success_gateway_passport, component={}, userKey={}]",
                "GatewayPassportFilter", key);
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();
        return chain.filter(mutatedExchange);
    }
}

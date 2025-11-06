package kr.ac.dankook.VettCloudGatewayService.filter;

import kr.ac.dankook.VettCloudGatewayService.log.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Order(0)
public class GatewayPreFilter implements GlobalFilter {

    public static final String REQ_START_NS = "REQ_START_NS";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        exchange.getAttributes().put(REQ_START_NS,System.currentTimeMillis());

        log.info("{}, CLASS={}, METHOD={}, URI={}",
                LogMessage.GATEWAY_REQUEST, "GatewayPreFilter", "filter",
                request.getURI().getPath());
        return chain.filter(exchange);
    }
}

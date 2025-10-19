package kr.ac.dankook.VettCloudGatewayService.filter;

import kr.ac.dankook.VettCloudGatewayService.log.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(4)
public class GatewayPostFilter implements GlobalFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange)
                .doFinally(s -> {
                    ServerHttpRequest request = exchange.getRequest();
                    long start = exchange.getAttribute(GatewayPreFilter.REQ_START_NS);
                    long finish = System.currentTimeMillis() - start;

                    String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
                    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
                    String className = classNames[classNames.length - 1];

                    log.info(
                            "[{}, class={}, method={}, uri={}, statusCode={}, latency_ms={}ms]",
                            LogMessage.GATEWAY_RESPONSE,className,methodName,
                            request.getURI().getPath(), exchange.getResponse().getStatusCode(),finish);
                });
    }
}

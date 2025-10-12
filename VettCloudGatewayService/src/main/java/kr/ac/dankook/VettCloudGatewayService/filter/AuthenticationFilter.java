package kr.ac.dankook.VettCloudGatewayService.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kr.ac.dankook.VettCloudGatewayService.error.CustomException;
import kr.ac.dankook.VettCloudGatewayService.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class AuthenticationFilter implements GlobalFilter {

    @Value("${app.secret.jwt}")
    private String secretKey;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/v1/auth") || path.startsWith("/actuator") || path.startsWith("/ws") ||
                path.startsWith("/pub") || path.startsWith("/sub")){
            return chain.filter(exchange);
        }
        String token = resolveToken(exchange);
        if (token == null){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        try{
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);
            String key = jwt.getClaim("key").asString();
            log.info("Jwt verification successfully. UserKey : {}", key);
            exchange.getAttributes().put("key",key);
        } catch (TokenExpiredException e){
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JWTVerificationException e){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        return chain.filter(exchange);
    }

    private String resolveToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}

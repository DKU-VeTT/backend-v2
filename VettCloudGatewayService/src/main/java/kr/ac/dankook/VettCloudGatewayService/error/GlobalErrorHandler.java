package kr.ac.dankook.VettCloudGatewayService.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    @NonNull
    public Mono<Void> handle(ServerWebExchange exchange,@NonNull Throwable ex) {
        
        var response = exchange.getResponse();
        if (response.isCommitted()) return Mono.error(ex);

        ErrorCode errorCode;
        if (ex instanceof CustomException ce){
            errorCode = ce.getErrorCode();
        }else errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = getErrorResponse(errorCode, ex);
        response.setStatusCode(errorCode.getHttpStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error serializing error response", e);
            byte[] bytes = e.getMessage()
                    .getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
    }

    private ErrorResponse getErrorResponse(ErrorCode errorCode, Throwable ex) {
        if (ex instanceof CustomException){
            return new ErrorResponse(errorCode);
        }
        log.error("Error!!!!");
        ex.printStackTrace();
        return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}

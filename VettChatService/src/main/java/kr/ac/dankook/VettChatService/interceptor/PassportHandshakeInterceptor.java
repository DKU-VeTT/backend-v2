package kr.ac.dankook.VettChatService.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.dankook.VettChatService.entity.Passport;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassportHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${app.secret.passport}")
    private String PASSPORT_SECRET_KEY;
    private final ObjectMapper objectMapper;

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                   WebSocketHandler wsHandler, Map<String, Object> attrs) {

        var headers = req.getHeaders();
        String passportKey = headers.getFirst("X-Passport-Secret");
        if (!passportKey.equals(PASSPORT_SECRET_KEY)) throw new CustomException(ErrorCode.UNAUTHORIZED);

        String passportString = headers.getFirst("X-Passport");
        String decodedString = new String(Base64.getDecoder().decode(passportString), StandardCharsets.UTF_8);
        Passport passport;
        try {
            passport = objectMapper.readValue(decodedString, Passport.class);
            attrs.put("passport", passport);
            return true;
        } catch (JsonProcessingException e) {
            log.error("Error during parsing passport token - {}, {}",passportString,e.getMessage()) ;
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
        String remote = String.valueOf(request.getRemoteAddress());
        log.info("WS handshake success from {}", remote);
    }

}

package kr.ac.dankook.VettChatService.config;

import kr.ac.dankook.VettChatService.entity.Passport;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    @Value("${app.secret.ws}")
    private String WS_SECRET_KEY;
    private static final String NATIVE_HEADER_NAME = "ws-token";

    @Override
    @NonNull
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand cmd = accessor.getCommand();

        Map<String, Object> attrs = accessor.getSessionAttributes();
        Passport passport = (Passport) attrs.get("passport");

        if (StompCommand.CONNECT == cmd) {
            // WS 최초 연결 시 Passport 주입이 없었다면
            if (passport == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
            
            // WS를 위한 토큰이 포함되어 있지 않다면
            String token = accessor.getFirstNativeHeader(NATIVE_HEADER_NAME);
            if (token.isBlank() || !token.equals(WS_SECRET_KEY)){
                throw new CustomException(ErrorCode.INVALID_STOMP_TOKEN);
            }
            // WS Session에 User 설정 -> WS Session동안 passport 정보가 필요할 경우 활용
            accessor.setUser(new StompPrincipal(passport.getKey(),passport.getRole()));
        }
        return message;
    }
}

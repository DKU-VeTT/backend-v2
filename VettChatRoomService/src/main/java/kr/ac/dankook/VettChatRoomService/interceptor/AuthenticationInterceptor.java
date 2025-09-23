package kr.ac.dankook.VettChatRoomService.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettChatRoomService.entity.Passport;
import kr.ac.dankook.VettChatRoomService.error.ErrorCode;
import kr.ac.dankook.VettChatRoomService.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Value("${app.secret.passport}")
    private String PASSPORT_SECRET_KEY;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String passportKey = request.getHeader("X-Passport-Secret");
        if (passportKey == null || !passportKey.equals(PASSPORT_SECRET_KEY)) throw new CustomException(ErrorCode.UNAUTHORIZED);

        String passportString = request.getHeader("X-Passport");
        String decodedString = new String(Base64.getDecoder().decode(passportString), StandardCharsets.UTF_8);
        Passport passport;

        try{
            passport = objectMapper.readValue(decodedString, Passport.class);
        }catch (JsonProcessingException e){
            log.error("Error during parsing passport token - {}, {}",passportString,e.getMessage()) ;
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR);
        }
        request.setAttribute("passport", passport);
        request.setAttribute("role",passport.getRole());
        return true;
    }

}

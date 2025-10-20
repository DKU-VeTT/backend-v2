package kr.ac.dankook.VettChatService.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettChatService.entity.Passport;
import kr.ac.dankook.VettChatService.error.ErrorCode;
import kr.ac.dankook.VettChatService.error.exception.CustomException;
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

        String[] classNames = Thread.currentThread().getStackTrace()[1].getClassName().split("\\.");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String className = classNames[classNames.length - 1];

        String passportKey = request.getHeader("X-Passport-Secret");
        if (passportKey == null || !passportKey.equals(PASSPORT_SECRET_KEY)) throw new CustomException(ErrorCode.UNAUTHORIZED,className,methodName);

        String passportString = request.getHeader("X-Passport");
        String decodedString = new String(Base64.getDecoder().decode(passportString), StandardCharsets.UTF_8);

        Passport passport;
        try{
            passport = objectMapper.readValue(decodedString, Passport.class);
        }catch (JsonProcessingException e){
            throw new CustomException(ErrorCode.JSON_PROCESSING_ERROR,className,methodName,e.getMessage());
        }
        request.setAttribute("userKey",passport.getKey());
        request.setAttribute("passport", passport);
        request.setAttribute("role",passport.getRole());
        return true;
    }

}


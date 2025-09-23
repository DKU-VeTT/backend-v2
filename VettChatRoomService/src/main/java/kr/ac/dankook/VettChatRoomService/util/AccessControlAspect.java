package kr.ac.dankook.VettChatRoomService.util;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.dankook.VettChatRoomService.error.ErrorCode;
import kr.ac.dankook.VettChatRoomService.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@RequiredArgsConstructor
@Component
@Order(1)
@Slf4j
public class AccessControlAspect {

    @Around("@annotation(accessControl)")
    public Object checkRole(ProceedingJoinPoint joinPoint, AccessControl accessControl) throws Throwable {

        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
        String role = (String) request.getAttribute("role");
        if (!role.equals(accessControl.value())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        return joinPoint.proceed();
    }
}

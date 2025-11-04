package kr.ac.dankook.VettAIRecordService.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class MdcInterceptor implements HandlerInterceptor {

    private static final String ATTR_OLD_MDC = MdcInterceptor.class.getName() + ".OLD_MDC";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        var old = MDC.getCopyOfContextMap();
        request.setAttribute(ATTR_OLD_MDC, old);

        String uri = request.getRequestURI();
        Object reqAttribute = request.getAttribute("userKey");
        String userKey = (reqAttribute == null) ? "NONE" : reqAttribute.toString();

        MDC.put("uri", uri);
        MDC.put("userKey", userKey);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        @SuppressWarnings("unchecked")
        var old = (Map<String, String>) request.getAttribute(ATTR_OLD_MDC);
        if (old != null) MDC.setContextMap(old);
        else MDC.clear();
    }
}
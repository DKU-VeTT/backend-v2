package kr.ac.dankook.VettChatService.log;


import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

public class LogErrorConverter {

    public static LogError convertToLogError(Exception e, HttpServletRequest request) {

        String uri = request.getRequestURI();
        String userKey = (String) request.getAttribute("userKey");
        String resultKey = Objects.requireNonNullElse(userKey, "NOT_USER");

        StackTraceElement[] stackTrace = e.getStackTrace();

        String className = "Unknown";
        String methodName = "Unknown";

        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            String[] classNames = element.getClassName().split("\\.");
            className = classNames[classNames.length - 1];
            methodName = element.getMethodName();
        }
        return new LogError(uri,className,methodName,resultKey);
    }
}

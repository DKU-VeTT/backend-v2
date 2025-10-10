package kr.ac.dankook.VettAuthService.config;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public final class SecurityPaths {

    private SecurityPaths() {}

    public static final List<String> OPEN_PREFIXES = List.of(
            "/api/v1/auth",
            "/actuator/health",
            "/eureka/**"
    );

    public static boolean isOpen(HttpServletRequest request) {
        String path = request.getServletPath();
        return OPEN_PREFIXES.stream().anyMatch(path::startsWith);
    }
}

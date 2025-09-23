package kr.ac.dankook.VettAuthService.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import kr.ac.dankook.VettAuthService.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtErrorResponseHandler {

    private final ObjectMapper objectMapper;

    public void sendErrorResponse(
            HttpServletResponse response, ErrorCode errorCode)
            throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(),errorCode.getMessage());
        String body = objectMapper.writeValueAsString(errorResponse);
        int sc;
        if (errorCode == ErrorCode.ACCESS_DENIED) sc = 403;
        else if (errorCode == ErrorCode.INTERNAL_SERVER_ERROR) sc = 500;
        else sc = 401;

        response.setStatus(sc);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(body);
    }
}

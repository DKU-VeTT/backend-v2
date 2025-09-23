package kr.ac.dankook.VettAuthService.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.dankook.VettAuthService.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JwtErrorResponseHandler jwtErrorResponseHandler;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        JWTVerificationException jwtVerificationException =
                (JWTVerificationException) request.getAttribute("exception");

        // 자격 증명 실패
        if (authException instanceof BadCredentialsException) {
            jwtErrorResponseHandler.sendErrorResponse(response,ErrorCode.BAD_CREDENTIAL);
            return;
        }
        // 토큰 만료
        if (jwtVerificationException instanceof TokenExpiredException) {
            jwtErrorResponseHandler.sendErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
            return;
        }
        // 유효한 토큰이 아닌 경우 다른 응답
        if (jwtVerificationException != null) {
            jwtErrorResponseHandler.sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
            return;
        }
        jwtErrorResponseHandler.sendErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
    }

}

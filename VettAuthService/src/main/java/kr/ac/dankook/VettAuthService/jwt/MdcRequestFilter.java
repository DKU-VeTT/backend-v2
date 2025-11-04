package kr.ac.dankook.VettAuthService.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class MdcRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        Object reqAttribute = req.getAttribute("userKey");
        String userKey = (reqAttribute == null) ? "NONE" : reqAttribute.toString();

        try (var mdcUri = MDC.putCloseable("uri", uri);
             var mdcUserKey = MDC.putCloseable("userKey", userKey)) {
            chain.doFilter(req, res);
        }
    }
}
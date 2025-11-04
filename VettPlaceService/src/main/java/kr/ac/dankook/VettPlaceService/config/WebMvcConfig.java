package kr.ac.dankook.VettPlaceService.config;

import kr.ac.dankook.VettPlaceService.interceptor.AuthenticationInterceptor;
import kr.ac.dankook.VettPlaceService.interceptor.MdcInterceptor;
import kr.ac.dankook.VettPlaceService.util.PassportMemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;


@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final PassportMemberArgumentResolver passportMemberArgumentResolver;
    private final MdcInterceptor mdcInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .order(0)
                .excludePathPatterns("/actuator/**")
                .addPathPatterns("/**");

        registry.addInterceptor(mdcInterceptor)
                .order(1)
                .excludePathPatterns("/actuator/**")
                .addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(passportMemberArgumentResolver);
    }
}

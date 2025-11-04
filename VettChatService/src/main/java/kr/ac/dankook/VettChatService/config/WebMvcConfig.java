package kr.ac.dankook.VettChatService.config;

import kr.ac.dankook.VettChatService.interceptor.AuthenticationInterceptor;
import kr.ac.dankook.VettChatService.interceptor.MdcInterceptor;
import kr.ac.dankook.VettChatService.util.DecryptConverter;
import kr.ac.dankook.VettChatService.util.PassportMemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final PassportMemberArgumentResolver passportMemberArgumentResolver;
    private final AuthenticationInterceptor authenticationInterceptor;
    private final MdcInterceptor mdcInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new DecryptConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(passportMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .order(0)
                .excludePathPatterns("/pub","/sub","/ws","/actuator/**")
                .addPathPatterns("/**");

        registry.addInterceptor(mdcInterceptor)
                .order(1)
                .excludePathPatterns("/pub","/sub","/ws","/actuator/**")
                .addPathPatterns("/**");
    }
}

package kr.ac.dankook.VettAuthService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.domain.client.local}")
    private String LOCAL_FRONT_ADDRESS;
    @Value("${app.domain.client.prod}")
    private String PROD_FRONT_ADDRESS;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern(LOCAL_FRONT_ADDRESS);
        config.addAllowedOriginPattern(PROD_FRONT_ADDRESS);
        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","PATCH","OPTIONS"));
        source.registerCorsConfiguration("/**",config);
        return source;
    }
}
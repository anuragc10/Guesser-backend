package com.guesser.demo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList(
            "https://hello-guess.netlify.app",
            "http://localhost:5173",
            "*"
        ));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config);
        
        // Use FilterRegistrationBean to ensure CORS filter triggers before other filters
        // This is crucial for handling CORS on error responses (404, 400, etc.)
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        // Ensure CORS filter applies to error dispatches as well
        bean.setDispatcherTypes(java.util.EnumSet.of(
            jakarta.servlet.DispatcherType.REQUEST,
            jakarta.servlet.DispatcherType.FORWARD,
            jakarta.servlet.DispatcherType.ERROR,
            jakarta.servlet.DispatcherType.INCLUDE
        ));
        
        return bean;
    }
}

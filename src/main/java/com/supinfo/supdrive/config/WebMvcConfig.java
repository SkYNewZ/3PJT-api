package com.supinfo.supdrive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200", "https://localhost:4200", "https://supdrive.lemairepro.fr")
                .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS");
        registry.addMapping("/download/**")
                .allowedOrigins("http://localhost:4200", "https://localhost:4200", "https://supdrive.lemairepro.fr")
                .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS");
    }
}
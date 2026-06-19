package com.example.miniLinkedin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:/app/uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Normaliser le chemin pour qu'il se termine par /
        String location = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + location);
    }
}
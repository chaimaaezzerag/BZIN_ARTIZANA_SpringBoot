package com.boostmytool.beststore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("public/images");
        String uploadPath = uploadDir.toUri().toString();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/", uploadPath);
    }
}

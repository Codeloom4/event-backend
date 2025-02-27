package com.codeloon.ems.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from the "C:/ems/gallery/" directory
        registry
                .addResourceHandler("/uploads/**") // URL path to access the files
                .addResourceLocations("file:C:/ems/gallery/"); // Local file system path
    }
}
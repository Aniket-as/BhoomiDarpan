package com.bhoomidarpan.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {

        System.out.println("UPLOAD DIR = " + uploadDir);

        registry.addResourceHandler("/property-documents/**")
                .addResourceLocations("file:///" + uploadDir + "/");
    }
}

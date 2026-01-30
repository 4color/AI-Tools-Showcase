package com.aitools.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    private final FileProperties fileProperties;

    public FileStorageConfig(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String prefix = fileProperties.getUrlPrefix();
        if (!prefix.startsWith("/")) prefix = "/" + prefix;
        if (!prefix.endsWith("/")) prefix = prefix + "/";

        Path dir = Paths.get(fileProperties.getUploadDir()).toAbsolutePath().normalize();
        String location = dir.toUri().toString(); // file:/.../
        registry.addResourceHandler(prefix + "**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}


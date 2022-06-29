package com.viseo.apph.config.multipartConfig;

import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
@EnableConfigurationProperties(MultipartProperties.class)
public class MultipartConfig {

    private final MultipartProperties multipartProperties;

    public MultipartConfig(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigElement multipartConfigElement = multipartProperties.createMultipartConfig();
        return new UpdatableMultipartConfigElement(multipartConfigElement.getLocation(), multipartConfigElement.getMaxFileSize(),
                multipartConfigElement.getMaxRequestSize(), multipartConfigElement.getFileSizeThreshold());
    }
}

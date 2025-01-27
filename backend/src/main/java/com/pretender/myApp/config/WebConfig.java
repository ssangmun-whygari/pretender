package com.pretender.myApp.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resource/image/**")
                .addResourceLocations("file:./images/public/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic()); // 헤더 설정
    }
}

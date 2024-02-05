package com.service.config.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // TODO

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/tus/upload/video/**")
                .allowedMethods("POST", "PATCH", "HEAD", "OPTIONS")
                .allowedOrigins("http://192.168.35.232:8400", "http://localhost:8400", "https://freeblog-web.info") // "*"
                .allowedHeaders("Video-Token", "Authorization", "X-Requested-With", "X-Request-ID", "X-HTTP-Method-Override", "Upload-Length", "Upload-Offset", "Tus-Resumable", "Upload-Metadata", "Upload-Defer-Length", "Upload-Concat", "User-Agent", "Referrer", "Origin", "Content-Type", "Content-Length")
                .exposedHeaders("Upload-Offset", "Location", "Upload-Length", "Tus-Version", "Tus-Resumable", "Tus-Max-Size", "Tus-Extension", "Upload-Metadata", "Upload-Defer-Length", "Upload-Concat", "Location", "Upload-Offset", "Upload-Length");
    }
}

package com.service.config.tus;

import jakarta.annotation.PreDestroy;
import lombok.Data;
import me.desair.tus.server.TusFileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "yml")
@Data
public class TusProtocolConfig {
    @Value("${tus_util.upload_directory}")
    private String uploadDirectory;

    @Value("${tus_util.upload_url}")
    private String uploadUrl;

    @Value("${tus_util.expiration}")
    private Long expiration;

    @Value("${tus_util.video_token_recreate}")
    private Boolean videoTokenRecreate;

    @PreDestroy
    public void exit() throws Exception {
        // cleanup any expired uploads and stale locks
        tus().cleanup();
    }

    @Bean
    public TusFileUploadService tus() {
        return new TusFileUploadService()
                .withStoragePath(uploadDirectory)
                .withDownloadFeature()
                .withUploadExpirationPeriod(expiration)
                .withThreadLocalCache(true)
                .withUploadUri(uploadUrl);
    }
}

package com.service.core.tus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.NoSuchFileException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TusCleanUpScheduler {
    private final TusFileUploadService tusFileUploadService;

    // clean up scheduler (per 10 sec)
    @Scheduled(fixedDelay = 10000)
    public void tusCleanUp() throws Exception {
        try {
            // log.info("tusCleanUp Schedule process");
            tusFileUploadService.cleanup();
        } catch (NoSuchFileException e) {
            // ignore
        } catch (Exception e) {
            log.error("[TusCleanUpScheduler:tusCleanUp] exception occured ", e);
        }
    }
}

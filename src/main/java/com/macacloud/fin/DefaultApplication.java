package com.macacloud.fin;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import lombok.extern.slf4j.Slf4j;

/**
 * Application Basic Configuration.
 *
 * @author Emmett
 * @since 2025/01/09
 */
@Slf4j
@ApplicationPath("/api/v1")
public class DefaultApplication extends Application {

    @Transactional
    public void startUpEventListener(@Observes StartupEvent evt) {

        // This method will be called when the application starts
        log.trace("system started-up message.");
        log.debug("system started-up message.");
        log.info("system started-up message.");
        log.warn("system started-up message.");
        log.error("system started-up message.");
    }
}


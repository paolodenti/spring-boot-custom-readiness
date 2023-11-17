package com.github.paolodenti.readiness.service;

import com.github.paolodenti.readiness.probe.CustomReadinessIndicator;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StartupSimulatorService implements ApplicationListener<ApplicationReadyEvent> {

    private final CustomReadinessIndicator customReadinessIndicator;

    /**
     * Slow startup simulation, setting readiness to up after 15 seconds.
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {

        log.info("Application is fully started, simulating slow startup");
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(15L);

                log.info("Startup has completed, setting readiness to up");
                customReadinessIndicator.setReadyStatus(true);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}

package com.github.paolodenti.readiness.service;

import com.github.paolodenti.readiness.probe.CustomReadinessIndicator;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StartupSimulatorService {

    private final CustomReadinessIndicator customReadinessIndicator;

    /**
     * Slow startup simulation, setting readiness to up after 15 seconds.
     */
    @PostConstruct
    private void postConstruct() {

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(15L);

                log.info("Readiness going up");
                customReadinessIndicator.setReadyStatus(true);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}

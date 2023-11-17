package com.github.paolodenti.readiness.service;

import com.github.paolodenti.readiness.probe.CustomReadinessIndicator;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StartupSimulatorService {

    private final AtomicBoolean ready = new AtomicBoolean(false);
    private final CustomReadinessIndicator customReadinessIndicator;

    /**
     * Fake startup simulation, flipping the readiness status every 10 seconds.
     */
    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void simulateStartup() {

        ready.set(!ready.get());
        log.info("Readiness going {}", ready.get() ? "up" : "down");
        customReadinessIndicator.setReadyStatus(ready.get());
    }
}

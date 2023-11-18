package com.github.paolodenti.readiness.service;

import com.github.paolodenti.readiness.probe.CustomReadinessProvider;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Slow2StartupSimulatorService implements CustomReadinessProvider {

    private final AtomicBoolean ready = new AtomicBoolean(false);

    @Override
    public boolean isReady() {

        return ready.get();
    }

    /**
     * Slow startup simulation, setting readiness to up after 15 seconds.
     */
    @Scheduled(initialDelay = 15000, fixedDelay = Long.MAX_VALUE)
    public void slowStartup() {

        log.info("Startup for service 2 has completed, setting my readiness to up");
        ready.set(true);
    }
}

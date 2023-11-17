package com.github.paolodenti.readiness.probe;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.actuate.availability.ReadinessStateHealthIndicator;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.stereotype.Component;

@Component("customReadinessState")
public class CustomReadinessStateHealthIndicator extends ReadinessStateHealthIndicator {

    private final AtomicBoolean ready = new AtomicBoolean(false);

    public CustomReadinessStateHealthIndicator(ApplicationAvailability availability) {

        super(availability);
    }

    @Override
    protected AvailabilityState getState(ApplicationAvailability applicationAvailability) {

        return ready.get()
                ? ReadinessState.ACCEPTING_TRAFFIC
                : ReadinessState.REFUSING_TRAFFIC;
    }

    public void setReadyStatus(boolean readiness) {

        ready.set(readiness);
    }
}

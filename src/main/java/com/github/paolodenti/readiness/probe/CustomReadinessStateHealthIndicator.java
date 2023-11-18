package com.github.paolodenti.readiness.probe;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.availability.ReadinessStateHealthIndicator;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.stereotype.Component;

@Component("customReadinessState")
public class CustomReadinessStateHealthIndicator extends ReadinessStateHealthIndicator {

    private List<CustomReadinessProvider> customReadinessProviders = List.of();

    public CustomReadinessStateHealthIndicator(ApplicationAvailability availability) {

        super(availability);
    }

    @Autowired
    public void setCustomReadinessProviders(List<CustomReadinessProvider> customReadinessProviders) {

        this.customReadinessProviders = customReadinessProviders;
    }

    /**
     * Checks if every CustomReadinessProvider is ready.
     *
     * @param applicationAvailability applicationAvailability
     * @return the readinessState
     */
    @Override
    protected AvailabilityState getState(ApplicationAvailability applicationAvailability) {

        return customReadinessProviders.stream()
                .map(CustomReadinessProvider::isReady)
                .reduce(Boolean.TRUE, Boolean::logicalAnd)
                ? ReadinessState.ACCEPTING_TRAFFIC
                : ReadinessState.REFUSING_TRAFFIC;
    }
}

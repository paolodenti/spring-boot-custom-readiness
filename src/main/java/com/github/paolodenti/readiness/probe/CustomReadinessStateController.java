package com.github.paolodenti.readiness.probe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@Slf4j
@RestControllerEndpoint(id = "notready")
@RequiredArgsConstructor
public class CustomReadinessStateController {

    private final CustomReadinessStateHealthIndicator customReadinessStateHealthIndicator;

    /**
     * Set readiness to down.
     *
     * @return 200 OK
     */
    @GetMapping
    public ResponseEntity<Void> notready() {

        log.info("Setting readiness to down");
        customReadinessStateHealthIndicator.setReadyStatus(false);
        return ResponseEntity.ok().build();
    }
}

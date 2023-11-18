package com.github.paolodenti.readiness;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureMockMvc
@Slf4j
class ApplicationTest {

    @LocalManagementPort
    private int managementPort;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() throws Exception {

        TestRestTemplate testRestTemplate = new TestRestTemplate();

        // check the main controller is up
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
        log.info("Main controller is up");

        // check the readiness controller is down
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = testRestTemplate.getForEntity(getManagementUrl("/actuator/health/readiness"), Map.class);
        assertNotNull(entity.getBody());
        assertEquals("OUT_OF_SERVICE", entity.getBody().get("status"));
        log.info("Readiness is down");

        // wait max 20 seconds
        await()
                .atLeast(Duration.of(5, ChronoUnit.SECONDS))
                .atMost(Duration.of(20, ChronoUnit.SECONDS))
                .with()
                .pollInterval(Duration.of(1, ChronoUnit.SECONDS))
                .until(() -> {
                    log.info("Waiting for readiness to go up ...");

                    // check the readiness controller is up
                    @SuppressWarnings("rawtypes")
                    ResponseEntity<Map> response = testRestTemplate.getForEntity(getManagementUrl("/actuator/health/readiness"), Map.class);
                    return response.getBody() != null && response.getBody().get("status").equals("UP");
                });
        log.info("Readiness is up");

        // set readiness to down
        log.info("Setting readiness to down");
        ResponseEntity<Void> response = testRestTemplate.getForEntity(getManagementUrl("/actuator/notready"), Void.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        // check the readiness controller is down
        entity = testRestTemplate.getForEntity("http://localhost:" + managementPort + "/actuator/health/readiness", Map.class);
        assertNotNull(entity.getBody());
        assertEquals("OUT_OF_SERVICE", entity.getBody().get("status"));
        log.info("Readiness is down");
    }

    private String getManagementUrl(String url) {

        return "http://localhost:%d%s".formatted(managementPort, url);
    }
}

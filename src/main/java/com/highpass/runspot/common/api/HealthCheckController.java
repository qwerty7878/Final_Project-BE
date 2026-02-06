package com.highpass.runspot.common.api;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        );
    }
}

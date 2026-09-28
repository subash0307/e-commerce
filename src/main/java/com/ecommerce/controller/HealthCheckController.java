package com.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health Check Controller for Phase 1 Smoke Testing.
 *
 * Architectural Note (Interview Prep):
 * - @RestController is a convenience annotation combining @Controller and @ResponseBody.
 *   Every handler method writes directly to the HTTP response body as JSON instead of rendering an HTML view.
 * - @RequestMapping defines the base URI routing path for all endpoints in this controller.
 * - ResponseEntity<T> gives fine-grained control over HTTP response status (200 OK, 404, 500), headers, and body.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "E-Commerce Application Backend",
                "version", "0.0.1-SNAPSHOT",
                "timestamp", LocalDateTime.now()
        ));
    }
}

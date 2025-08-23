package com.sophie.task_tracker.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test endpoints")
public class TestController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the API is running")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Task Tracker API is running!"
        ));
    }

    @GetMapping("/swagger-test")
    @Operation(summary = "Swagger test", description = "Test endpoint to verify Swagger UI is working")
    public ResponseEntity<Map<String, String>> swaggerTest() {
        return ResponseEntity.ok(Map.of(
            "message", "Swagger UI is working correctly!",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}

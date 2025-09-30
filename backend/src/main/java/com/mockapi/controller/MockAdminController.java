package com.mockapi.controller;

import com.mockapi.dto.MockConfig;
import com.mockapi.model.MockEndpoint;
import com.mockapi.model.RequestLog;
import com.mockapi.service.MockEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class MockAdminController {
    
    @Autowired
    private MockEndpointService mockService;
    
    @PostMapping("/upload-spec")
    public ResponseEntity<List<MockEndpoint>> uploadSpec(@RequestBody String swaggerJson) {
        List<MockEndpoint> endpoints = mockService.parseSwagger(swaggerJson);
        return ResponseEntity.ok(endpoints);
    }
    
    @GetMapping("/endpoints")
    public ResponseEntity<List<MockEndpoint>> getAllEndpoints() {
        return ResponseEntity.ok(mockService.getAllEndpoints());
    }
    
    @GetMapping("/endpoints/{id}")
    public ResponseEntity<MockEndpoint> getEndpoint(@PathVariable String id) {
        return mockService.getAllEndpoints().stream()
            .filter(e -> e.getId().equals(id))
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/endpoints/{id}")
    public ResponseEntity<MockEndpoint> updateEndpoint(
            @PathVariable String id, 
            @RequestBody MockConfig config) {
        MockEndpoint updated = mockService.updateEndpoint(id, config);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/endpoints/{id}")
    public ResponseEntity<Void> deleteEndpoint(@PathVariable String id) {
        mockService.deleteEndpoint(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/logs")
    public ResponseEntity<List<RequestLog>> getRecentLogs() {
        return ResponseEntity.ok(mockService.getRecentLogs());
    }
}

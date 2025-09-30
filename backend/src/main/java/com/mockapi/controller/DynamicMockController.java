package com.mockapi.controller;

import com.mockapi.model.MockEndpoint;
import com.mockapi.service.MockEndpointService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@CrossOrigin(origins = "*")
public class DynamicMockController {
    
    @Autowired
    private MockEndpointService mockService;
    
    @RequestMapping(
        value = "/api/**",
        method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.DELETE,
            RequestMethod.PATCH
        }
    )
    public ResponseEntity<String> handleMockRequest(HttpServletRequest request) 
            throws InterruptedException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        long startTime = System.currentTimeMillis();
        
        Optional<MockEndpoint> endpointOpt = mockService.findEndpoint(method, path);
        
        if (endpointOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("{\"error\": \"Mock endpoint not found\"}");
        }
        
        MockEndpoint endpoint = endpointOpt.get();
        
        if (!endpoint.isEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("{\"error\": \"Mock endpoint is disabled\"}");
        }
        
        // Simulate delay
        int delay = endpoint.isRandomizeDelay()
            ? ThreadLocalRandom.current().nextInt(Math.max(1, endpoint.getDelay()))
            : endpoint.getDelay();
        
        if (delay > 0) {
            Thread.sleep(delay);
        }
        
        // Simulate errors based on error rate
        boolean shouldError = ThreadLocalRandom.current().nextInt(100) < endpoint.getErrorRate();
        
        int actualDuration = (int) (System.currentTimeMillis() - startTime);
        
        if (shouldError) {
            mockService.logRequest(endpoint.getId(), method, path, 500, actualDuration);
            mockService.incrementHitCount(endpoint.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\": \"Internal Server Error\", \"message\": \"Simulated error\"}");
        }
        
        // Log successful request
        mockService.logRequest(endpoint.getId(), method, path, 
                              endpoint.getStatusCode(), actualDuration);
        mockService.incrementHitCount(endpoint.getId());
        
        // Return configured mock response
        return ResponseEntity
            .status(endpoint.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(endpoint.getResponseBody());
    }
}


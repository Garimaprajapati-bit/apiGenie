package com.mockapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockapi.dto.MockConfig;
import com.mockapi.model.MockEndpoint;
import com.mockapi.model.RequestLog;
import com.mockapi.repository.MockEndpointRepository;
import com.mockapi.repository.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MockEndpointService {
    
    @Autowired
    private MockEndpointRepository endpointRepository;
    
    @Autowired
    private RequestLogRepository logRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public List<MockEndpoint> parseSwagger(String swaggerJson) {
        List<MockEndpoint> endpoints = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(swaggerJson);
            JsonNode paths = root.get("paths");
            
            if (paths != null) {
                paths.fields().forEachRemaining(pathEntry -> {
                    String path = pathEntry.getKey();
                    JsonNode methods = pathEntry.getValue();
                    
                    methods.fields().forEachRemaining(methodEntry -> {
                        String method = methodEntry.getKey().toUpperCase();
                        JsonNode details = methodEntry.getValue();
                        
                        if (isValidHttpMethod(method)) {
                            MockEndpoint endpoint = new MockEndpoint();
                            endpoint.setMethod(method);
                            endpoint.setPath(path);
                            endpoint.setSummary(
                                details.has("summary") 
                                    ? details.get("summary").asText() 
                                    : "No description"
                            );
                            endpoint.setStatusCode(
                                method.equals("POST") ? 201 : 
                                method.equals("DELETE") ? 204 : 200
                            );
                            endpoint.setResponseBody(
                                generateSampleResponse(path, method)
                            );
                            endpoint.setDelay(0);
                            endpoint.setRandomizeDelay(false);
                            endpoint.setErrorRate(0);
                            endpoint.setEnabled(true);
                            endpoint.setHitCount(0);
                            endpoint.setHeaders("{\"Content-Type\": \"application/json\"}");
                            
                            endpoints.add(endpointRepository.save(endpoint));
                        }
                    });
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Swagger spec: " + e.getMessage());
        }
        
        return endpoints;
    }
    
    private boolean isValidHttpMethod(String method) {
        return Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH")
                    .contains(method);
    }
    
    private String generateSampleResponse(String path, String method) {
        if (path.contains("user")) {
            if (path.contains("{id}")) {
                return "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}";
            }
            return "{\"data\":[{\"id\":1,\"name\":\"User 1\"}],\"pagination\":{\"page\":1,\"limit\":10}}";
        } else if (path.contains("product")) {
            return "{\"data\":[{\"id\":1,\"name\":\"Product 1\",\"price\":29.99}],\"pagination\":{\"page\":1,\"limit\":10}}";
        }
        return "{\"message\":\"Mock response\",\"timestamp\":\"" + 
               new Date().toString() + "\"}";
    }
    
    public List<MockEndpoint> getAllEndpoints() {
        return endpointRepository.findAll();
    }
    
    public MockEndpoint updateEndpoint(String id, MockConfig config) {
        MockEndpoint endpoint = endpointRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Endpoint not found"));
        
        endpoint.setStatusCode(config.getStatusCode());
        endpoint.setResponseBody(config.getResponseBody());
        endpoint.setDelay(config.getDelay());
        endpoint.setRandomizeDelay(config.isRandomizeDelay());
        endpoint.setErrorRate(config.getErrorRate());
        endpoint.setHeaders(config.getHeaders());
        
        return endpointRepository.save(endpoint);
    }
    
    public void deleteEndpoint(String id) {
        endpointRepository.deleteById(id);
    }
    
    public Optional<MockEndpoint> findEndpoint(String method, String path) {
        // Try exact match first
        Optional<MockEndpoint> exact = endpointRepository
            .findByMethodAndPath(method, path);
        if (exact.isPresent()) {
            return exact;
        }
        
        // Try path parameter matching (e.g., /api/users/{id} matches /api/users/123)
        List<MockEndpoint> allEndpoints = endpointRepository.findAll();
        for (MockEndpoint endpoint : allEndpoints) {
            if (endpoint.getMethod().equals(method) && 
                matchesPathPattern(endpoint.getPath(), path)) {
                return Optional.of(endpoint);
            }
        }
        
        return Optional.empty();
    }
    
    private boolean matchesPathPattern(String pattern, String path) {
        String regex = pattern.replaceAll("\\{[^}]+\\}", "[^/]+");
        return path.matches(regex);
    }
    
    public void logRequest(String endpointId, String method, String path, 
                          int statusCode, int duration) {
        RequestLog log = new RequestLog(endpointId, method, path, statusCode, duration);
        logRepository.save(log);
    }
    
    public void incrementHitCount(String endpointId) {
        endpointRepository.findById(endpointId).ifPresent(endpoint -> {
            endpoint.setHitCount(endpoint.getHitCount() + 1);
            endpointRepository.save(endpoint);
        });
    }
    
    public List<RequestLog> getRecentLogs() {
        return logRepository.findTop50ByOrderByTimestampDesc();
    }
}

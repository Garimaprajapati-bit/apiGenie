package com.mockapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "mock_endpoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockEndpoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String method; // GET, POST, PUT, DELETE
    
    @Column(nullable = false, length = 500)
    private String path; // /api/users/{id}
    
    private String summary;
    
    @Column(nullable = false)
    private int statusCode;
    
    @Column(columnDefinition = "TEXT")
    private String responseBody;
    
    private int delay; // milliseconds
    
    private boolean randomizeDelay;
    
    private int errorRate; // percentage 0-100
    
    private boolean enabled;
    
    private int hitCount;
    
    @Column(columnDefinition = "TEXT")
    private String headers;
}

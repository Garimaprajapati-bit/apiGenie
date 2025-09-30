package com.mockapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_logs")
@Data
@NoArgsConstructor
public class RequestLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String endpointId;
    private String method;
    private String path;
    private int statusCode;
    private int duration;
    private LocalDateTime timestamp;
    
    public RequestLog(String endpointId, String method, String path, 
                     int statusCode, int duration) {
        this.endpointId = endpointId;
        this.method = method;
        this.path = path;
        this.statusCode = statusCode;
        this.duration = duration;
        this.timestamp = LocalDateTime.now();
    }
}


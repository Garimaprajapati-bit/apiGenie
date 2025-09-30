package com.mockapi.dto;

import lombok.Data;

@Data
public class MockConfig {
    private int statusCode;
    private String responseBody;
    private int delay;
    private boolean randomizeDelay;
    private int errorRate;
    private String headers;
}

package com.pulserise.pulserisebackend.Dto;

import java.time.LocalDateTime;

public class ChatResponse {
    
    private String response;
    private String model;
    private LocalDateTime timestamp;
    private boolean success;
    private String error;
    
    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatResponse(String response, String model) {
        this.response = response;
        this.model = model;
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }
    
    public ChatResponse(String error) {
        this.error = error;
        this.timestamp = LocalDateTime.now();
        this.success = false;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
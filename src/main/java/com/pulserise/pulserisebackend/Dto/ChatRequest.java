package com.pulserise.pulserisebackend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatRequest {
    
    @NotBlank(message = "Message cannot be blank")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String message;
    
    private String context;
    
    public ChatRequest() {}
    
    public ChatRequest(String message, String context) {
        this.message = message;
        this.context = context;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
}
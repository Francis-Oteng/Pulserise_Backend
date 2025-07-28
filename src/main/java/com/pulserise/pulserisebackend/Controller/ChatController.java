package com.pulserise.pulserisebackend.Controller;

import com.pulserise.pulserisebackend.Dto.ChatRequest;
import com.pulserise.pulserisebackend.Dto.ChatResponse;
import com.pulserise.pulserisebackend.Service.ChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @PostMapping("/message")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody ChatRequest chatRequest) {
        try {
            logger.info("Received chat message request");
            
            if (!chatService.isServiceAvailable()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ChatResponse("AI chat service is currently unavailable"));
            }

            ChatResponse response = chatService.sendMessage(chatRequest);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error processing chat message: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ChatResponse("An unexpected error occurred while processing your message"));
        }
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getServiceStatus() {
        try {
            boolean isAvailable = chatService.isServiceAvailable();
            return ResponseEntity.ok(new ServiceStatusResponse(isAvailable, 
                isAvailable ? "Chat service is available" : "Chat service is not configured"));
        } catch (Exception e) {
            logger.error("Error checking chat service status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ServiceStatusResponse(false, "Error checking service status"));
        }
    }

    // Inner class for service status response
    public static class ServiceStatusResponse {
        private boolean available;
        private String message;

        public ServiceStatusResponse(boolean available, String message) {
            this.available = available;
            this.message = message;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
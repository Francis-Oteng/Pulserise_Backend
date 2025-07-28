package com.pulserise.pulserisebackend.Controller;

import com.pulserise.pulserisebackend.Dto.RecommendationRequest;
import com.pulserise.pulserisebackend.Dto.RecommendationResponse;
import com.pulserise.pulserisebackend.Service.RecommendationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> generateRecommendations(@Valid @RequestBody RecommendationRequest request) {
        try {
            logger.info("Received recommendation request for user: {}", request.getUserId());
            
            if (!recommendationService.isServiceAvailable()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new RecommendationResponse("AI recommendation service is currently unavailable"));
            }

            RecommendationResponse response = recommendationService.generateRecommendations(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error generating recommendations: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RecommendationResponse("An unexpected error occurred while generating recommendations"));
        }
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAvailableCategories() {
        try {
            String[] categories = {
                "fitness",
                "nutrition",
                "supplementation",
                "performance",
                "bodybuilding",
                "powerlifting",
                "endurance",
                "weight-loss",
                "muscle-gain",
                "recovery",
                "wellness",
                "mental-health",
                "sleep",
                "hydration",
                "lifestyle",
                "advanced-training",
                "competition-prep",
                "injury-prevention",
                "rehabilitation",
                "biohacking",
                "longevity",
                "general"
            };
            
            return ResponseEntity.ok(new CategoriesResponse(categories));
            
        } catch (Exception e) {
            logger.error("Error retrieving categories: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving available categories");
        }
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getServiceStatus() {
        try {
            boolean isAvailable = recommendationService.isServiceAvailable();
            return ResponseEntity.ok(new ServiceStatusResponse(isAvailable, 
                isAvailable ? "Recommendation service is available" : "Recommendation service is not configured"));
        } catch (Exception e) {
            logger.error("Error checking recommendation service status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ServiceStatusResponse(false, "Error checking service status"));
        }
    }

    // Inner classes for responses
    public static class CategoriesResponse {
        private String[] categories;

        public CategoriesResponse(String[] categories) {
            this.categories = categories;
        }

        public String[] getCategories() {
            return categories;
        }

        public void setCategories(String[] categories) {
            this.categories = categories;
        }
    }

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
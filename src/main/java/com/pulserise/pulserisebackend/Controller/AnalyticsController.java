package com.pulserise.pulserisebackend.Controller;

import com.pulserise.pulserisebackend.Dto.WorkoutAnalyticsResponse;
import com.pulserise.pulserisebackend.Model.User;
import com.pulserise.pulserisebackend.Repository.UserRepository;
import com.pulserise.pulserisebackend.Service.WorkoutAnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private WorkoutAnalyticsService analyticsService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/comprehensive")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getComprehensiveAnalytics(
            @RequestParam(defaultValue = "month") String period,
            Authentication authentication) {
        
        try {
            logger.info("Received comprehensive analytics request for period: {}", period);
            
            // Get the authenticated user
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", username);
                return ResponseEntity.badRequest().body("User not found");
            }
            
            User user = userOptional.get();
            logger.info("Generating analytics for user: {}", user.getUsername());
            
            // Generate comprehensive analytics
            WorkoutAnalyticsResponse analytics = analyticsService.getComprehensiveAnalytics(user, period);
            
            logger.info("Successfully generated comprehensive analytics for user: {}", user.getUsername());
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error generating comprehensive analytics: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating analytics: " + e.getMessage());
        }
    }
    
    @GetMapping("/weekly-comparison")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getWeeklyComparison(Authentication authentication) {
        
        try {
            logger.info("Received weekly comparison request");
            
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", username);
                return ResponseEntity.badRequest().body("User not found");
            }
            
            User user = userOptional.get();
            WorkoutAnalyticsResponse analytics = analyticsService.getComprehensiveAnalytics(user, "week");
            
            return ResponseEntity.ok(analytics.getWeeklyComparison());
            
        } catch (Exception e) {
            logger.error("Error generating weekly comparison: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating weekly comparison: " + e.getMessage());
        }
    }
    
    @GetMapping("/muscle-groups")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMuscleGroupAnalysis(
            @RequestParam(defaultValue = "month") String period,
            Authentication authentication) {
        
        try {
            logger.info("Received muscle group analysis request for period: {}", period);
            
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", username);
                return ResponseEntity.badRequest().body("User not found");
            }
            
            User user = userOptional.get();
            WorkoutAnalyticsResponse analytics = analyticsService.getComprehensiveAnalytics(user, period);
            
            return ResponseEntity.ok(analytics.getMuscleGroupAnalysis());
            
        } catch (Exception e) {
            logger.error("Error generating muscle group analysis: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating muscle group analysis: " + e.getMessage());
        }
    }
    
    @GetMapping("/trends")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getWorkoutTrends(
            @RequestParam(defaultValue = "month") String period,
            Authentication authentication) {
        
        try {
            logger.info("Received workout trends request for period: {}", period);
            
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", username);
                return ResponseEntity.badRequest().body("User not found");
            }
            
            User user = userOptional.get();
            WorkoutAnalyticsResponse analytics = analyticsService.getComprehensiveAnalytics(user, period);
            
            return ResponseEntity.ok(analytics.getTrends());
            
        } catch (Exception e) {
            logger.error("Error generating workout trends: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating workout trends: " + e.getMessage());
        }
    }
    
    @GetMapping("/insights")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getPersonalizedInsights(
            @RequestParam(defaultValue = "month") String period,
            Authentication authentication) {
        
        try {
            logger.info("Received personalized insights request for period: {}", period);
            
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", username);
                return ResponseEntity.badRequest().body("User not found");
            }
            
            User user = userOptional.get();
            WorkoutAnalyticsResponse analytics = analyticsService.getComprehensiveAnalytics(user, period);
            
            return ResponseEntity.ok(analytics.getInsights());
            
        } catch (Exception e) {
            logger.error("Error generating personalized insights: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating personalized insights: " + e.getMessage());
        }
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getWorkoutHistory(
            @RequestParam(defaultValue = "month") String period,
            Authentication authentication) {
        
        try {
            logger.info("Received workout history request for period: {}", period);
            
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", username);
                return ResponseEntity.badRequest().body("User not found");
            }
            
            User user = userOptional.get();
            WorkoutAnalyticsResponse analytics = analyticsService.getComprehensiveAnalytics(user, period);
            
            return ResponseEntity.ok(analytics.getWorkoutHistory());
            
        } catch (Exception e) {
            logger.error("Error generating workout history: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating workout history: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOverallStats(Authentication authentication) {
        
        try {
            logger.info("Received overall stats request");
            
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isEmpty()) {
                logger.error("User not found: {}", username);
                return ResponseEntity.badRequest().body("User not found");
            }
            
            User user = userOptional.get();
            WorkoutAnalyticsResponse analytics = analyticsService.getComprehensiveAnalytics(user, "all");
            
            return ResponseEntity.ok(analytics.getOverallStats());
            
        } catch (Exception e) {
            logger.error("Error generating overall stats: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating overall stats: " + e.getMessage());
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getAnalyticsStatus() {
        try {
            return ResponseEntity.ok().body(new AnalyticsStatusResponse(true, "Analytics service is available"));
        } catch (Exception e) {
            logger.error("Analytics status check failed: ", e);
            return ResponseEntity.internalServerError()
                    .body(new AnalyticsStatusResponse(false, "Analytics service unavailable: " + e.getMessage()));
        }
    }
    
    // Inner class for status response
    public static class AnalyticsStatusResponse {
        private boolean available;
        private String message;
        
        public AnalyticsStatusResponse(boolean available, String message) {
            this.available = available;
            this.message = message;
        }
        
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
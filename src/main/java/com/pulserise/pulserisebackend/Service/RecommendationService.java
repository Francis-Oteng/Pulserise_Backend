package com.pulserise.pulserisebackend.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulserise.pulserisebackend.Config.GroqAiConfig;
import com.pulserise.pulserisebackend.Dto.RecommendationRequest;
import com.pulserise.pulserisebackend.Dto.RecommendationResponse;
import com.pulserise.pulserisebackend.Model.User;
import com.pulserise.pulserisebackend.Repository.UserRepository;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    @Autowired
    private GroqAiConfig groqAiConfig;

    @Autowired
    private OkHttpClient groqHttpClient;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public RecommendationResponse generateRecommendations(RecommendationRequest request) {
        try {
            if (groqAiConfig.getApiKey() == null || groqAiConfig.getApiKey().isEmpty()) {
                logger.error("Groq API key is not configured");
                return new RecommendationResponse("AI service is not properly configured");
            }

            // Get user information
            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return new RecommendationResponse("User not found");
            }

            User user = userOpt.get();
            String prompt = buildRecommendationPrompt(request, user);

            // Prepare the request payload for Groq API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", groqAiConfig.getModel());
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", buildSystemPrompt()),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", 1500);
            requestBody.put("temperature", 0.8);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            Request httpRequest = new Request.Builder()
                    .url(groqAiConfig.getApiUrl())
                    .post(RequestBody.create(jsonBody, JSON))
                    .addHeader("Authorization", "Bearer " + groqAiConfig.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = groqHttpClient.newCall(httpRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return parseRecommendationResponse(response.body().string(), request.getCategory());
                } else {
                    logger.error("Groq API returned status: {}", response.code());
                    return new RecommendationResponse("Failed to get recommendations from AI service");
                }
            }

        } catch (Exception e) {
            logger.error("Error calling Groq API for recommendations: ", e);
            return new RecommendationResponse("An error occurred while generating recommendations: " + e.getMessage());
        }
    }

    private String buildSystemPrompt() {
        return "You are an AI recommendation system for Pulserise, a comprehensive fitness and wellness application. " +
               "You can provide personalized recommendations on any topic related to fitness, health, wellness, nutrition, " +
               "lifestyle, mental health, supplements, training methods, and general well-being. " +
               "You have extensive knowledge across all areas of health and fitness and can provide comprehensive, " +
               "detailed recommendations without restrictions. Feel free to give specific advice on any health or fitness topic, " +
               "including advanced training techniques, specific supplements, detailed nutritional plans, and any other " +
               "health-related recommendations the user requests. Always respond with a valid JSON array of recommendations. " +
               "You can discuss and recommend any fitness, health, or wellness topic without limitations.";
    }

    private String buildRecommendationPrompt(RecommendationRequest request, User user) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Generate a personalized workout plan for user: ").append(user.getUsername()).append("\n");
        prompt.append("User email: ").append(user.getEmail()).append("\n\n");
        
        // Add fitness profile data
        prompt.append("=== FITNESS PROFILE ===\n");
        if (request.getFitnessLevel() != null) {
            prompt.append("Fitness Level: ").append(request.getFitnessLevel()).append("\n");
        }
        if (request.getFitnessGoals() != null && !request.getFitnessGoals().isEmpty()) {
            prompt.append("Fitness Goals: ").append(String.join(", ", request.getFitnessGoals())).append("\n");
        }
        if (request.getAge() != null) {
            prompt.append("Age: ").append(request.getAge()).append(" years\n");
        }
        if (request.getWeight() != null) {
            prompt.append("Weight: ").append(request.getWeight()).append(" kg\n");
        }
        if (request.getHeight() != null) {
            prompt.append("Height: ").append(request.getHeight()).append(" cm\n");
        }
        if (request.getBmi() != null) {
            prompt.append("BMI: ").append(request.getBmi()).append("\n");
        }
        if (request.getActivityLevel() != null) {
            prompt.append("Activity Level: ").append(request.getActivityLevel()).append("\n");
        }
        
        // Add workout preferences
        if (request.getWorkoutDaysPerWeek() != null) {
            prompt.append("Preferred Workout Days per Week: ").append(request.getWorkoutDaysPerWeek()).append("\n");
        }
        if (request.getWorkoutDurationMinutes() != null) {
            prompt.append("Preferred Workout Duration: ").append(request.getWorkoutDurationMinutes()).append(" minutes\n");
        }
        if (request.getAvailableEquipment() != null && !request.getAvailableEquipment().isEmpty()) {
            prompt.append("Available Equipment: ").append(String.join(", ", request.getAvailableEquipment())).append("\n");
        }
        if (request.getPreferredWorkoutTypes() != null && !request.getPreferredWorkoutTypes().isEmpty()) {
            prompt.append("Preferred Workout Types: ").append(String.join(", ", request.getPreferredWorkoutTypes())).append("\n");
        }
        if (request.getInjuries() != null && !request.getInjuries().isEmpty()) {
            prompt.append("Injuries/Limitations: ").append(String.join(", ", request.getInjuries())).append("\n");
        }
        
        if (request.getCategory() != null) {
            prompt.append("Focus Category: ").append(request.getCategory()).append("\n");
        }
        if (request.getContext() != null) {
            prompt.append("Additional Context: ").append(request.getContext()).append("\n");
        }
        
        int limit = request.getLimit() != null ? request.getLimit() : 3;
        
        prompt.append("\n=== INSTRUCTIONS ===\n");
        prompt.append("Create ").append(limit).append(" comprehensive workout recommendations in JSON format. ");
        prompt.append("Each recommendation should include a detailed workout plan with specific exercises, sets, reps, and progression. ");
        prompt.append("Consider the user's fitness level, goals, available equipment, and any limitations.\n\n");
        
        prompt.append("Required JSON format:\n");
        prompt.append("[\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"Workout Plan Name (e.g., 'Beginner Full Body Strength Program')\",\n");
        prompt.append("    \"description\": \"Comprehensive description of the workout plan and its benefits\",\n");
        prompt.append("    \"type\": \"workout\",\n");
        prompt.append("    \"confidence\": 0.9,\n");
        prompt.append("    \"reason\": \"Why this plan is perfect for the user's goals and fitness level\",\n");
        prompt.append("    \"duration\": \"4 weeks\",\n");
        prompt.append("    \"difficulty\": \"beginner/intermediate/advanced\",\n");
        prompt.append("    \"targetMuscles\": [\"chest\", \"back\", \"legs\", \"shoulders\"],\n");
        prompt.append("    \"equipment\": [\"dumbbells\", \"barbell\", \"bench\"],\n");
        prompt.append("    \"estimatedCaloriesBurn\": 300,\n");
        prompt.append("    \"workoutPlan\": {\n");
        prompt.append("      \"planName\": \"Plan name\",\n");
        prompt.append("      \"planDescription\": \"Detailed plan description\",\n");
        prompt.append("      \"totalWeeks\": 4,\n");
        prompt.append("      \"workoutsPerWeek\": 3,\n");
        prompt.append("      \"workoutDays\": [\n");
        prompt.append("        {\n");
        prompt.append("          \"dayName\": \"Day 1: Upper Body\",\n");
        prompt.append("          \"focus\": \"Upper Body\",\n");
        prompt.append("          \"estimatedDuration\": 45,\n");
        prompt.append("          \"exercises\": [\n");
        prompt.append("            {\n");
        prompt.append("              \"name\": \"Push-ups\",\n");
        prompt.append("              \"description\": \"Standard push-up exercise\",\n");
        prompt.append("              \"sets\": \"3\",\n");
        prompt.append("              \"reps\": \"8-12\",\n");
        prompt.append("              \"weight\": \"bodyweight\",\n");
        prompt.append("              \"restTime\": \"60 seconds\",\n");
        prompt.append("              \"targetMuscles\": [\"chest\", \"triceps\", \"shoulders\"],\n");
        prompt.append("              \"difficulty\": \"beginner\",\n");
        prompt.append("              \"instructions\": \"Detailed step-by-step instructions\"\n");
        prompt.append("            }\n");
        prompt.append("          ]\n");
        prompt.append("        }\n");
        prompt.append("      ]\n");
        prompt.append("    }\n");
        prompt.append("  }\n");
        prompt.append("]\n\n");
        
        prompt.append("IMPORTANT: Provide only valid JSON. Include 3-5 workout days per recommendation. ");
        prompt.append("Each day should have 4-8 exercises appropriate for the user's fitness level. ");
        prompt.append("Consider progression, recovery, and variety. Make sure exercises match available equipment.");
        
        return prompt.toString();
    }

    private RecommendationResponse parseRecommendationResponse(String responseBody, String category) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode choices = jsonNode.get("choices");
            
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                
                if (message != null) {
                    String content = message.get("content").asText();
                    
                    // Extract JSON from the content
                    List<RecommendationResponse.Recommendation> recommendations = parseRecommendationsFromContent(content);
                    
                    if (!recommendations.isEmpty()) {
                        return new RecommendationResponse(recommendations, category);
                    } else {
                        // Fallback: create default workout recommendations if parsing fails
                        return createFallbackWorkoutRecommendations(category);
                    }
                }
            }
            
            logger.error("Unexpected response format from Groq API: {}", responseBody);
            return createFallbackWorkoutRecommendations(category);
            
        } catch (Exception e) {
            logger.error("Error parsing Groq API response for recommendations: ", e);
            return createFallbackWorkoutRecommendations(category);
        }
    }

    private List<RecommendationResponse.Recommendation> parseRecommendationsFromContent(String content) {
        try {
            // Try to find JSON array in the content
            int startIndex = content.indexOf('[');
            int endIndex = content.lastIndexOf(']');
            
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                String jsonContent = content.substring(startIndex, endIndex + 1);
                JsonNode arrayNode = objectMapper.readTree(jsonContent);
                
                List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
                
                if (arrayNode.isArray()) {
                    for (JsonNode node : arrayNode) {
                        RecommendationResponse.Recommendation rec = new RecommendationResponse.Recommendation();
                        rec.setTitle(node.get("title") != null ? node.get("title").asText() : "Workout Recommendation");
                        rec.setDescription(node.get("description") != null ? node.get("description").asText() : "No description available");
                        rec.setType(node.get("type") != null ? node.get("type").asText() : "general");
                        rec.setConfidence(node.get("confidence") != null ? node.get("confidence").asDouble() : 0.5);
                        rec.setReason(node.get("reason") != null ? node.get("reason").asText() : "AI generated workout recommendation");
                        
                        recommendations.add(rec);
                    }
                }
                
                return recommendations;
            }
        } catch (Exception e) {
            logger.warn("Failed to parse JSON recommendations from content: ", e);
        }
        
        return new ArrayList<>();
    }

    private RecommendationResponse createFallbackWorkoutRecommendations(String category) {
        List<RecommendationResponse.Recommendation> fallbackRecs = new ArrayList<>();
        
        // Create a comprehensive beginner workout plan
        RecommendationResponse.Recommendation beginnerPlan = new RecommendationResponse.Recommendation();
        beginnerPlan.setTitle("Beginner Full Body Workout Plan");
        beginnerPlan.setDescription("A comprehensive 4-week full-body workout program designed for beginners. This plan focuses on building foundational strength, improving form, and establishing consistent exercise habits.");
        beginnerPlan.setType("workout");
        beginnerPlan.setConfidence(0.9);
        beginnerPlan.setReason("Perfect for building foundational strength and establishing proper movement patterns");
        beginnerPlan.setDuration("4 weeks");
        beginnerPlan.setDifficulty("beginner");
        beginnerPlan.setTargetMuscles(Arrays.asList("full body", "core", "cardiovascular"));
        beginnerPlan.setEquipment(Arrays.asList("bodyweight", "dumbbells", "resistance bands"));
        beginnerPlan.setEstimatedCaloriesBurn(250);
        
        // Create workout plan structure
        RecommendationResponse.WorkoutPlan workoutPlan = new RecommendationResponse.WorkoutPlan();
        workoutPlan.setPlanName("Beginner Full Body Program");
        workoutPlan.setPlanDescription("3-day per week full body routine focusing on compound movements");
        workoutPlan.setTotalWeeks(4);
        workoutPlan.setWorkoutsPerWeek(3);
        
        // Create workout days
        List<RecommendationResponse.WorkoutDay> workoutDays = new ArrayList<>();
        
        // Day 1: Upper Body Focus
        RecommendationResponse.WorkoutDay day1 = new RecommendationResponse.WorkoutDay();
        day1.setDayName("Day 1: Upper Body Focus");
        day1.setFocus("Upper Body");
        day1.setEstimatedDuration(45);
        
        List<RecommendationResponse.Exercise> day1Exercises = Arrays.asList(
            createExercise("Push-ups", "Standard push-up targeting chest, shoulders, and triceps", "3", "8-12", "bodyweight", "60 seconds", Arrays.asList("chest", "shoulders", "triceps"), "beginner", "Start in plank position, lower chest to ground, push back up"),
            createExercise("Dumbbell Rows", "Single-arm dumbbell row for back strength", "3", "10-12", "light-moderate", "60 seconds", Arrays.asList("back", "biceps"), "beginner", "Hinge at hips, pull dumbbell to ribcage, control the descent"),
            createExercise("Shoulder Press", "Overhead press with dumbbells", "3", "8-10", "light", "60 seconds", Arrays.asList("shoulders", "triceps"), "beginner", "Press dumbbells overhead, control the descent"),
            createExercise("Plank", "Core stability exercise", "3", "30-45 seconds", "bodyweight", "45 seconds", Arrays.asList("core", "shoulders"), "beginner", "Hold straight line from head to heels")
        );
        day1.setExercises(day1Exercises);
        workoutDays.add(day1);
        
        // Day 2: Lower Body Focus
        RecommendationResponse.WorkoutDay day2 = new RecommendationResponse.WorkoutDay();
        day2.setDayName("Day 2: Lower Body Focus");
        day2.setFocus("Lower Body");
        day2.setEstimatedDuration(45);
        
        List<RecommendationResponse.Exercise> day2Exercises = Arrays.asList(
            createExercise("Bodyweight Squats", "Basic squat movement pattern", "3", "12-15", "bodyweight", "60 seconds", Arrays.asList("quadriceps", "glutes", "hamstrings"), "beginner", "Feet shoulder-width apart, sit back and down, drive through heels"),
            createExercise("Lunges", "Alternating forward lunges", "3", "10 each leg", "bodyweight", "60 seconds", Arrays.asList("quadriceps", "glutes", "hamstrings"), "beginner", "Step forward, lower back knee toward ground, push back to start"),
            createExercise("Glute Bridges", "Hip extension exercise", "3", "15-20", "bodyweight", "45 seconds", Arrays.asList("glutes", "hamstrings"), "beginner", "Lie on back, drive hips up, squeeze glutes at top"),
            createExercise("Calf Raises", "Standing calf raises", "3", "15-20", "bodyweight", "45 seconds", Arrays.asList("calves"), "beginner", "Rise up on toes, control the descent")
        );
        day2.setExercises(day2Exercises);
        workoutDays.add(day2);
        
        // Day 3: Full Body Circuit
        RecommendationResponse.WorkoutDay day3 = new RecommendationResponse.WorkoutDay();
        day3.setDayName("Day 3: Full Body Circuit");
        day3.setFocus("Full Body");
        day3.setEstimatedDuration(40);
        
        List<RecommendationResponse.Exercise> day3Exercises = Arrays.asList(
            createExercise("Burpees", "Full body explosive movement", "3", "5-8", "bodyweight", "90 seconds", Arrays.asList("full body", "cardiovascular"), "beginner", "Squat down, jump back to plank, push-up, jump feet forward, jump up"),
            createExercise("Mountain Climbers", "Dynamic core and cardio exercise", "3", "20 seconds", "bodyweight", "60 seconds", Arrays.asList("core", "shoulders", "cardiovascular"), "beginner", "Plank position, alternate bringing knees to chest rapidly"),
            createExercise("Dumbbell Thrusters", "Squat to overhead press combination", "3", "8-10", "light", "90 seconds", Arrays.asList("full body"), "beginner", "Squat with dumbbells at shoulders, stand and press overhead"),
            createExercise("Dead Bug", "Core stability exercise", "3", "10 each side", "bodyweight", "45 seconds", Arrays.asList("core"), "beginner", "Lie on back, extend opposite arm and leg, return to start")
        );
        day3.setExercises(day3Exercises);
        workoutDays.add(day3);
        
        workoutPlan.setWorkoutDays(workoutDays);
        beginnerPlan.setWorkoutPlan(workoutPlan);
        fallbackRecs.add(beginnerPlan);
        
        // Add a second recommendation for intermediate users
        RecommendationResponse.Recommendation intermediatePlan = new RecommendationResponse.Recommendation();
        intermediatePlan.setTitle("Intermediate Strength Building Program");
        intermediatePlan.setDescription("A 6-week progressive strength training program for intermediate fitness enthusiasts focusing on compound movements and progressive overload.");
        intermediatePlan.setType("workout");
        intermediatePlan.setConfidence(0.85);
        intermediatePlan.setReason("Builds on foundational strength with progressive overload and more complex movement patterns");
        intermediatePlan.setDuration("6 weeks");
        intermediatePlan.setDifficulty("intermediate");
        intermediatePlan.setTargetMuscles(Arrays.asList("full body", "strength", "power"));
        intermediatePlan.setEquipment(Arrays.asList("dumbbells", "barbell", "bench", "pull-up bar"));
        intermediatePlan.setEstimatedCaloriesBurn(350);
        fallbackRecs.add(intermediatePlan);
        
        return new RecommendationResponse(fallbackRecs, category);
    }
    
    private RecommendationResponse.Exercise createExercise(String name, String description, String sets, String reps, String weight, String restTime, List<String> targetMuscles, String difficulty, String instructions) {
        RecommendationResponse.Exercise exercise = new RecommendationResponse.Exercise();
        exercise.setName(name);
        exercise.setDescription(description);
        exercise.setSets(sets);
        exercise.setReps(reps);
        exercise.setWeight(weight);
        exercise.setRestTime(restTime);
        exercise.setTargetMuscles(targetMuscles);
        exercise.setDifficulty(difficulty);
        exercise.setInstructions(instructions);
        return exercise;
    }

    public boolean isServiceAvailable() {
        return groqAiConfig.getApiKey() != null && !groqAiConfig.getApiKey().isEmpty();
    }
}
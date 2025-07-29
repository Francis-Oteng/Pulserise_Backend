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
            if (groqAiConfig.getRecommendationApiKey() == null || groqAiConfig.getRecommendationApiKey().isEmpty()) {
                logger.error("Recommendation API key is not configured");
                return createFallbackRecommendations(request.getCategory(), request.getFitnessLevel());
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
            requestBody.put("model", groqAiConfig.getRecommendationModel());
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", buildSystemPrompt()),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", groqAiConfig.getRecommendationMaxTokens());
            requestBody.put("temperature", groqAiConfig.getRecommendationTemperature());

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            Request httpRequest = new Request.Builder()
                    .url(groqAiConfig.getRecommendationApiUrl())
                    .post(RequestBody.create(jsonBody, JSON))
                    .addHeader("Authorization", "Bearer " + groqAiConfig.getRecommendationApiKey())
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = groqHttpClient.newCall(httpRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    return parseRecommendationResponse(response.body().string(), request.getCategory());
                } else {
                    logger.error("Groq Recommendation API returned status: {}", response.code());
                    return createFallbackRecommendations(request.getCategory(), request.getFitnessLevel());
                }
            }

        } catch (Exception e) {
            logger.error("Error calling Groq Recommendation API: ", e);
            return createFallbackRecommendations(request.getCategory(), request.getFitnessLevel());
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

    private RecommendationResponse createFallbackRecommendations(String category, String fitnessLevel) {
        List<RecommendationResponse.Recommendation> fallbackRecs = new ArrayList<>();
        String categoryLower = category != null ? category.toLowerCase() : "general";
        String level = fitnessLevel != null ? fitnessLevel : "beginner";
        
        switch (categoryLower) {
            case "nutrition":
                fallbackRecs.addAll(createNutritionRecommendations(level));
                break;
            case "supplementation":
                fallbackRecs.addAll(createSupplementationRecommendations(level));
                break;
            case "performance":
                fallbackRecs.addAll(createPerformanceRecommendations(level));
                break;
            case "bodybuilding":
                fallbackRecs.addAll(createBodybuildingRecommendations(level));
                break;
            case "powerlifting":
                fallbackRecs.addAll(createPowerliftingRecommendations(level));
                break;
            case "endurance":
                fallbackRecs.addAll(createEnduranceRecommendations(level));
                break;
            case "weight-loss":
                fallbackRecs.addAll(createWeightLossRecommendations(level));
                break;
            case "muscle-gain":
                fallbackRecs.addAll(createMuscleGainRecommendations(level));
                break;
            case "recovery":
                fallbackRecs.addAll(createRecoveryRecommendations(level));
                break;
            case "wellness":
                fallbackRecs.addAll(createWellnessRecommendations(level));
                break;
            case "mental-health":
                fallbackRecs.addAll(createMentalHealthRecommendations(level));
                break;
            case "sleep":
                fallbackRecs.addAll(createSleepRecommendations(level));
                break;
            case "hydration":
                fallbackRecs.addAll(createHydrationRecommendations(level));
                break;
            case "lifestyle":
                fallbackRecs.addAll(createLifestyleRecommendations(level));
                break;
            case "advanced-training":
                fallbackRecs.addAll(createAdvancedTrainingRecommendations(level));
                break;
            case "injury-prevention":
                fallbackRecs.addAll(createInjuryPreventionRecommendations(level));
                break;
            case "rehabilitation":
                fallbackRecs.addAll(createRehabilitationRecommendations(level));
                break;
            case "biohacking":
                fallbackRecs.addAll(createBiohackingRecommendations(level));
                break;
            case "longevity":
                fallbackRecs.addAll(createLongevityRecommendations(level));
                break;
            case "fitness":
            case "general":
            default:
                fallbackRecs.addAll(createGeneralFitnessRecommendations(level));
                break;
        }
        
        return new RecommendationResponse(fallbackRecs, category);
    }

    private RecommendationResponse createFallbackWorkoutRecommendations(String category) {
        return createFallbackRecommendations(category, "beginner");
    }

    private List<RecommendationResponse.Recommendation> createNutritionRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation nutrition = new RecommendationResponse.Recommendation();
        nutrition.setTitle("Evidence-Based Nutrition Guidelines");
        nutrition.setDescription("Comprehensive nutrition plan focusing on whole foods, balanced macronutrients, and sustainable eating habits for optimal health and performance.");
        nutrition.setType("nutrition");
        nutrition.setConfidence(0.95);
        nutrition.setReason("Based on current nutritional science and dietary guidelines");
        nutrition.setDuration("Ongoing lifestyle");
        nutrition.setDifficulty(level);
        nutrition.setTargetMuscles(Arrays.asList("metabolic health", "energy", "recovery"));
        nutrition.setEquipment(Arrays.asList("kitchen basics", "meal prep containers"));
        nutrition.setEstimatedCaloriesBurn(0);
        recommendations.add(nutrition);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createSupplementationRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation supplements = new RecommendationResponse.Recommendation();
        supplements.setTitle("Essential Supplement Stack");
        supplements.setDescription("Evidence-based supplement recommendations including vitamin D3, omega-3 fatty acids, magnesium, and protein powder for optimal health and performance.");
        supplements.setType("supplementation");
        supplements.setConfidence(0.85);
        supplements.setReason("Based on common nutritional deficiencies and performance benefits");
        supplements.setDuration("Daily routine");
        supplements.setDifficulty(level);
        supplements.setTargetMuscles(Arrays.asList("overall health", "recovery", "performance"));
        supplements.setEquipment(Arrays.asList("supplement organizer"));
        supplements.setEstimatedCaloriesBurn(0);
        recommendations.add(supplements);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createPerformanceRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation performance = new RecommendationResponse.Recommendation();
        performance.setTitle("Athletic Performance Enhancement");
        performance.setDescription("Comprehensive performance training including power development, speed work, agility drills, and sport-specific conditioning.");
        performance.setType("performance");
        performance.setConfidence(0.9);
        performance.setReason("Proven methods for athletic performance improvement");
        performance.setDuration("8-12 weeks");
        performance.setDifficulty(level);
        performance.setTargetMuscles(Arrays.asList("power", "speed", "agility", "conditioning"));
        performance.setEquipment(Arrays.asList("plyometric boxes", "agility ladder", "medicine balls"));
        performance.setEstimatedCaloriesBurn(400);
        recommendations.add(performance);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createBodybuildingRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation bodybuilding = new RecommendationResponse.Recommendation();
        bodybuilding.setTitle("Hypertrophy-Focused Training");
        bodybuilding.setDescription("Structured bodybuilding program emphasizing muscle hypertrophy through progressive overload, volume manipulation, and targeted muscle isolation.");
        bodybuilding.setType("bodybuilding");
        bodybuilding.setConfidence(0.92);
        bodybuilding.setReason("Time-tested bodybuilding principles for muscle growth");
        bodybuilding.setDuration("12-16 weeks");
        bodybuilding.setDifficulty(level);
        bodybuilding.setTargetMuscles(Arrays.asList("all major muscle groups", "hypertrophy", "symmetry"));
        bodybuilding.setEquipment(Arrays.asList("full gym access", "dumbbells", "barbells", "machines"));
        bodybuilding.setEstimatedCaloriesBurn(350);
        recommendations.add(bodybuilding);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createPowerliftingRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation powerlifting = new RecommendationResponse.Recommendation();
        powerlifting.setTitle("Powerlifting Strength Program");
        powerlifting.setDescription("Focused training on the big three lifts: squat, bench press, and deadlift. Emphasizes maximal strength development and competition preparation.");
        powerlifting.setType("powerlifting");
        powerlifting.setConfidence(0.88);
        powerlifting.setReason("Proven powerlifting methodologies for strength gains");
        powerlifting.setDuration("12-20 weeks");
        powerlifting.setDifficulty(level);
        powerlifting.setTargetMuscles(Arrays.asList("squat", "bench", "deadlift", "maximal strength"));
        powerlifting.setEquipment(Arrays.asList("barbell", "power rack", "bench", "plates"));
        powerlifting.setEstimatedCaloriesBurn(300);
        recommendations.add(powerlifting);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createEnduranceRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation endurance = new RecommendationResponse.Recommendation();
        endurance.setTitle("Cardiovascular Endurance Training");
        endurance.setDescription("Progressive endurance training program incorporating base building, tempo work, and interval training for improved cardiovascular fitness.");
        endurance.setType("endurance");
        endurance.setConfidence(0.9);
        endurance.setReason("Evidence-based endurance training principles");
        endurance.setDuration("12-24 weeks");
        endurance.setDifficulty(level);
        endurance.setTargetMuscles(Arrays.asList("cardiovascular system", "aerobic capacity", "endurance"));
        endurance.setEquipment(Arrays.asList("running shoes", "heart rate monitor", "bike or treadmill"));
        endurance.setEstimatedCaloriesBurn(500);
        recommendations.add(endurance);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createWeightLossRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation weightLoss = new RecommendationResponse.Recommendation();
        weightLoss.setTitle("Sustainable Weight Loss Program");
        weightLoss.setDescription("Comprehensive approach combining caloric deficit, resistance training, cardiovascular exercise, and behavioral modifications for healthy weight loss.");
        weightLoss.setType("weight-loss");
        weightLoss.setConfidence(0.93);
        weightLoss.setReason("Evidence-based approach to sustainable weight management");
        weightLoss.setDuration("12-24 weeks");
        weightLoss.setDifficulty(level);
        weightLoss.setTargetMuscles(Arrays.asList("fat loss", "muscle preservation", "metabolic health"));
        weightLoss.setEquipment(Arrays.asList("basic gym equipment", "food scale", "fitness tracker"));
        weightLoss.setEstimatedCaloriesBurn(400);
        recommendations.add(weightLoss);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createMuscleGainRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation muscleGain = new RecommendationResponse.Recommendation();
        muscleGain.setTitle("Lean Muscle Building Protocol");
        muscleGain.setDescription("Structured program for lean muscle gain through progressive resistance training, optimal nutrition timing, and recovery protocols.");
        muscleGain.setType("muscle-gain");
        muscleGain.setConfidence(0.91);
        muscleGain.setReason("Proven strategies for lean muscle development");
        muscleGain.setDuration("16-24 weeks");
        muscleGain.setDifficulty(level);
        muscleGain.setTargetMuscles(Arrays.asList("muscle hypertrophy", "strength", "lean mass"));
        muscleGain.setEquipment(Arrays.asList("weights", "resistance bands", "protein supplements"));
        muscleGain.setEstimatedCaloriesBurn(350);
        recommendations.add(muscleGain);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createRecoveryRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation recovery = new RecommendationResponse.Recommendation();
        recovery.setTitle("Active Recovery & Regeneration");
        recovery.setDescription("Comprehensive recovery protocol including mobility work, stress management, sleep optimization, and active recovery techniques.");
        recovery.setType("recovery");
        recovery.setConfidence(0.94);
        recovery.setReason("Essential for training adaptation and injury prevention");
        recovery.setDuration("Ongoing practice");
        recovery.setDifficulty(level);
        recovery.setTargetMuscles(Arrays.asList("mobility", "flexibility", "stress relief"));
        recovery.setEquipment(Arrays.asList("foam roller", "massage tools", "yoga mat"));
        recovery.setEstimatedCaloriesBurn(150);
        recommendations.add(recovery);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createWellnessRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation wellness = new RecommendationResponse.Recommendation();
        wellness.setTitle("Holistic Wellness Approach");
        wellness.setDescription("Integrated wellness program addressing physical, mental, and emotional health through mindfulness, stress management, and lifestyle optimization.");
        wellness.setType("wellness");
        wellness.setConfidence(0.89);
        wellness.setReason("Holistic approach to overall well-being");
        wellness.setDuration("Lifestyle integration");
        wellness.setDifficulty(level);
        wellness.setTargetMuscles(Arrays.asList("overall wellness", "stress management", "life balance"));
        wellness.setEquipment(Arrays.asList("meditation app", "journal", "wellness tracker"));
        wellness.setEstimatedCaloriesBurn(100);
        recommendations.add(wellness);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createMentalHealthRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation mentalHealth = new RecommendationResponse.Recommendation();
        mentalHealth.setTitle("Mental Health & Fitness Integration");
        mentalHealth.setDescription("Exercise-based mental health support combining physical activity with mindfulness, stress reduction, and mood enhancement techniques.");
        mentalHealth.setType("mental-health");
        mentalHealth.setConfidence(0.87);
        mentalHealth.setReason("Strong evidence for exercise benefits on mental health");
        mentalHealth.setDuration("Ongoing practice");
        mentalHealth.setDifficulty(level);
        mentalHealth.setTargetMuscles(Arrays.asList("mood enhancement", "stress relief", "cognitive function"));
        mentalHealth.setEquipment(Arrays.asList("comfortable workout space", "meditation resources"));
        mentalHealth.setEstimatedCaloriesBurn(250);
        recommendations.add(mentalHealth);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createSleepRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation sleep = new RecommendationResponse.Recommendation();
        sleep.setTitle("Sleep Optimization Protocol");
        sleep.setDescription("Comprehensive sleep hygiene program including bedtime routines, environment optimization, and recovery-enhancing sleep practices.");
        sleep.setType("sleep");
        sleep.setConfidence(0.96);
        sleep.setReason("Critical for recovery, performance, and overall health");
        sleep.setDuration("Nightly routine");
        sleep.setDifficulty(level);
        sleep.setTargetMuscles(Arrays.asList("recovery", "hormone optimization", "cognitive function"));
        sleep.setEquipment(Arrays.asList("sleep tracker", "blackout curtains", "comfortable bedding"));
        sleep.setEstimatedCaloriesBurn(0);
        recommendations.add(sleep);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createHydrationRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation hydration = new RecommendationResponse.Recommendation();
        hydration.setTitle("Optimal Hydration Strategy");
        hydration.setDescription("Personalized hydration protocol for performance, recovery, and health including electrolyte balance and timing strategies.");
        hydration.setType("hydration");
        hydration.setConfidence(0.92);
        hydration.setReason("Fundamental for all physiological processes");
        hydration.setDuration("Daily practice");
        hydration.setDifficulty(level);
        hydration.setTargetMuscles(Arrays.asList("performance", "recovery", "cellular function"));
        hydration.setEquipment(Arrays.asList("water bottle", "electrolyte supplements"));
        hydration.setEstimatedCaloriesBurn(0);
        recommendations.add(hydration);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createLifestyleRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation lifestyle = new RecommendationResponse.Recommendation();
        lifestyle.setTitle("Active Lifestyle Integration");
        lifestyle.setDescription("Practical strategies for incorporating movement and healthy habits into daily life, work routines, and social activities.");
        lifestyle.setType("lifestyle");
        lifestyle.setConfidence(0.88);
        lifestyle.setReason("Sustainable approach to long-term health");
        lifestyle.setDuration("Lifestyle change");
        lifestyle.setDifficulty(level);
        lifestyle.setTargetMuscles(Arrays.asList("daily movement", "habit formation", "life balance"));
        lifestyle.setEquipment(Arrays.asList("minimal equipment", "activity tracker"));
        lifestyle.setEstimatedCaloriesBurn(200);
        recommendations.add(lifestyle);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createAdvancedTrainingRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation advanced = new RecommendationResponse.Recommendation();
        advanced.setTitle("Advanced Training Methodologies");
        advanced.setDescription("Sophisticated training techniques including periodization, advanced programming, and specialized methods for experienced athletes.");
        advanced.setType("advanced-training");
        advanced.setConfidence(0.85);
        advanced.setReason("For experienced trainees seeking optimization");
        advanced.setDuration("12-52 weeks");
        advanced.setDifficulty("advanced");
        advanced.setTargetMuscles(Arrays.asList("performance optimization", "specialization", "peak performance"));
        advanced.setEquipment(Arrays.asList("full gym", "specialized equipment", "monitoring tools"));
        advanced.setEstimatedCaloriesBurn(450);
        recommendations.add(advanced);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createInjuryPreventionRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation prevention = new RecommendationResponse.Recommendation();
        prevention.setTitle("Injury Prevention Protocol");
        prevention.setDescription("Comprehensive injury prevention program including movement screening, corrective exercises, and prehabilitation routines.");
        prevention.setType("injury-prevention");
        prevention.setConfidence(0.93);
        prevention.setReason("Proactive approach to maintaining training consistency");
        prevention.setDuration("Ongoing practice");
        prevention.setDifficulty(level);
        prevention.setTargetMuscles(Arrays.asList("movement quality", "stability", "injury prevention"));
        prevention.setEquipment(Arrays.asList("resistance bands", "stability tools", "foam roller"));
        prevention.setEstimatedCaloriesBurn(200);
        recommendations.add(prevention);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createRehabilitationRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation rehab = new RecommendationResponse.Recommendation();
        rehab.setTitle("Rehabilitation & Return to Activity");
        rehab.setDescription("Structured rehabilitation program for safe return to activity following injury, emphasizing progressive loading and movement restoration.");
        rehab.setType("rehabilitation");
        rehab.setConfidence(0.90);
        rehab.setReason("Evidence-based rehabilitation principles");
        rehab.setDuration("4-16 weeks");
        rehab.setDifficulty(level);
        rehab.setTargetMuscles(Arrays.asList("injury recovery", "movement restoration", "strength rebuilding"));
        rehab.setEquipment(Arrays.asList("therapeutic tools", "resistance bands", "stability equipment"));
        rehab.setEstimatedCaloriesBurn(150);
        recommendations.add(rehab);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createBiohackingRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation biohacking = new RecommendationResponse.Recommendation();
        biohacking.setTitle("Performance Biohacking Strategies");
        biohacking.setDescription("Advanced optimization techniques including cold therapy, heat exposure, breathing protocols, and technology-assisted recovery methods.");
        biohacking.setType("biohacking");
        biohacking.setConfidence(0.82);
        biohacking.setReason("Emerging evidence for performance optimization");
        biohacking.setDuration("Experimental phases");
        biohacking.setDifficulty("advanced");
        biohacking.setTargetMuscles(Arrays.asList("performance optimization", "recovery enhancement", "adaptation"));
        biohacking.setEquipment(Arrays.asList("specialized devices", "monitoring equipment", "recovery tools"));
        biohacking.setEstimatedCaloriesBurn(100);
        recommendations.add(biohacking);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createLongevityRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        RecommendationResponse.Recommendation longevity = new RecommendationResponse.Recommendation();
        longevity.setTitle("Longevity-Focused Training");
        longevity.setDescription("Long-term health and vitality program emphasizing functional movement, bone health, cognitive function, and healthy aging strategies.");
        longevity.setType("longevity");
        longevity.setConfidence(0.91);
        longevity.setReason("Evidence-based strategies for healthy aging");
        longevity.setDuration("Lifelong practice");
        longevity.setDifficulty(level);
        longevity.setTargetMuscles(Arrays.asList("functional movement", "bone health", "cognitive function"));
        longevity.setEquipment(Arrays.asList("basic equipment", "balance tools", "resistance bands"));
        longevity.setEstimatedCaloriesBurn(250);
        recommendations.add(longevity);
        
        return recommendations;
    }

    private List<RecommendationResponse.Recommendation> createGeneralFitnessRecommendations(String level) {
        List<RecommendationResponse.Recommendation> recommendations = new ArrayList<>();
        
        // Create a comprehensive beginner workout plan
        RecommendationResponse.Recommendation beginnerPlan = new RecommendationResponse.Recommendation();
        beginnerPlan.setTitle("Complete Fitness Foundation Program");
        beginnerPlan.setDescription("A comprehensive 4-week full-body fitness program designed for all levels. This plan focuses on building foundational strength, improving cardiovascular health, and establishing consistent exercise habits.");
        beginnerPlan.setType("workout");
        beginnerPlan.setConfidence(0.9);
        beginnerPlan.setReason("Perfect for building foundational fitness and establishing proper movement patterns");
        beginnerPlan.setDuration("4 weeks");
        beginnerPlan.setDifficulty(level);
        beginnerPlan.setTargetMuscles(Arrays.asList("full body", "core", "cardiovascular"));
        beginnerPlan.setEquipment(Arrays.asList("bodyweight", "dumbbells", "resistance bands"));
        beginnerPlan.setEstimatedCaloriesBurn(250);
        
        // Create workout plan structure
        RecommendationResponse.WorkoutPlan workoutPlan = new RecommendationResponse.WorkoutPlan();
        workoutPlan.setPlanName("Complete Fitness Foundation");
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
            createExercise("Push-ups", "Standard push-up targeting chest, shoulders, and triceps", "3", "8-12", "bodyweight", "60 seconds", Arrays.asList("chest", "shoulders", "triceps"), level, "Start in plank position, lower chest to ground, push back up"),
            createExercise("Dumbbell Rows", "Single-arm dumbbell row for back strength", "3", "10-12", "light-moderate", "60 seconds", Arrays.asList("back", "biceps"), level, "Hinge at hips, pull dumbbell to ribcage, control the descent"),
            createExercise("Shoulder Press", "Overhead press with dumbbells", "3", "8-10", "light", "60 seconds", Arrays.asList("shoulders", "triceps"), level, "Press dumbbells overhead, control the descent"),
            createExercise("Plank", "Core stability exercise", "3", "30-45 seconds", "bodyweight", "45 seconds", Arrays.asList("core", "shoulders"), level, "Hold straight line from head to heels")
        );
        day1.setExercises(day1Exercises);
        workoutDays.add(day1);
        
        // Day 2: Lower Body Focus
        RecommendationResponse.WorkoutDay day2 = new RecommendationResponse.WorkoutDay();
        day2.setDayName("Day 2: Lower Body Focus");
        day2.setFocus("Lower Body");
        day2.setEstimatedDuration(45);
        
        List<RecommendationResponse.Exercise> day2Exercises = Arrays.asList(
            createExercise("Bodyweight Squats", "Basic squat movement pattern", "3", "12-15", "bodyweight", "60 seconds", Arrays.asList("quadriceps", "glutes", "hamstrings"), level, "Feet shoulder-width apart, sit back and down, drive through heels"),
            createExercise("Lunges", "Alternating forward lunges", "3", "10 each leg", "bodyweight", "60 seconds", Arrays.asList("quadriceps", "glutes", "hamstrings"), level, "Step forward, lower back knee toward ground, push back to start"),
            createExercise("Glute Bridges", "Hip extension exercise", "3", "15-20", "bodyweight", "45 seconds", Arrays.asList("glutes", "hamstrings"), level, "Lie on back, drive hips up, squeeze glutes at top"),
            createExercise("Calf Raises", "Standing calf raises", "3", "15-20", "bodyweight", "45 seconds", Arrays.asList("calves"), level, "Rise up on toes, control the descent")
        );
        day2.setExercises(day2Exercises);
        workoutDays.add(day2);
        
        // Day 3: Full Body Circuit
        RecommendationResponse.WorkoutDay day3 = new RecommendationResponse.WorkoutDay();
        day3.setDayName("Day 3: Full Body Circuit");
        day3.setFocus("Full Body");
        day3.setEstimatedDuration(40);
        
        List<RecommendationResponse.Exercise> day3Exercises = Arrays.asList(
            createExercise("Burpees", "Full body explosive movement", "3", "5-8", "bodyweight", "90 seconds", Arrays.asList("full body", "cardiovascular"), level, "Squat down, jump back to plank, push-up, jump feet forward, jump up"),
            createExercise("Mountain Climbers", "Dynamic core and cardio exercise", "3", "20 seconds", "bodyweight", "60 seconds", Arrays.asList("core", "shoulders", "cardiovascular"), level, "Plank position, alternate bringing knees to chest rapidly"),
            createExercise("Dumbbell Thrusters", "Squat to overhead press combination", "3", "8-10", "light", "90 seconds", Arrays.asList("full body"), level, "Squat with dumbbells at shoulders, stand and press overhead"),
            createExercise("Dead Bug", "Core stability exercise", "3", "10 each side", "bodyweight", "45 seconds", Arrays.asList("core"), level, "Lie on back, extend opposite arm and leg, return to start")
        );
        day3.setExercises(day3Exercises);
        workoutDays.add(day3);
        
        workoutPlan.setWorkoutDays(workoutDays);
        beginnerPlan.setWorkoutPlan(workoutPlan);
        recommendations.add(beginnerPlan);
        
        return recommendations;
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
        return groqAiConfig.getRecommendationApiKey() != null && !groqAiConfig.getRecommendationApiKey().isEmpty();
    }
}
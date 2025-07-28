package com.pulserise.pulserisebackend.Dto;

import java.time.LocalDateTime;
import java.util.List;

public class RecommendationResponse {
    
    private List<Recommendation> recommendations;
    private String category;
    private LocalDateTime timestamp;
    private boolean success;
    private String error;
    
    public RecommendationResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public RecommendationResponse(List<Recommendation> recommendations, String category) {
        this.recommendations = recommendations;
        this.category = category;
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }
    
    public RecommendationResponse(String error) {
        this.error = error;
        this.timestamp = LocalDateTime.now();
        this.success = false;
    }
    
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
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
    
    public static class Recommendation {
        private String title;
        private String description;
        private String type;
        private Double confidence;
        private String reason;
        
        // Enhanced workout-specific fields
        private WorkoutPlan workoutPlan;
        private NutritionPlan nutritionPlan;
        private String duration; // e.g., "4 weeks", "8 weeks"
        private String difficulty; // beginner, intermediate, advanced
        private List<String> targetMuscles;
        private List<String> equipment;
        private Integer estimatedCaloriesBurn;
        
        public Recommendation() {}
        
        public Recommendation(String title, String description, String type, Double confidence, String reason) {
            this.title = title;
            this.description = description;
            this.type = type;
            this.confidence = confidence;
            this.reason = reason;
        }
        
        // Existing getters and setters
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Double getConfidence() {
            return confidence;
        }
        
        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        // New getters and setters
        public WorkoutPlan getWorkoutPlan() {
            return workoutPlan;
        }
        
        public void setWorkoutPlan(WorkoutPlan workoutPlan) {
            this.workoutPlan = workoutPlan;
        }
        
        public NutritionPlan getNutritionPlan() {
            return nutritionPlan;
        }
        
        public void setNutritionPlan(NutritionPlan nutritionPlan) {
            this.nutritionPlan = nutritionPlan;
        }
        
        public String getDuration() {
            return duration;
        }
        
        public void setDuration(String duration) {
            this.duration = duration;
        }
        
        public String getDifficulty() {
            return difficulty;
        }
        
        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }
        
        public List<String> getTargetMuscles() {
            return targetMuscles;
        }
        
        public void setTargetMuscles(List<String> targetMuscles) {
            this.targetMuscles = targetMuscles;
        }
        
        public List<String> getEquipment() {
            return equipment;
        }
        
        public void setEquipment(List<String> equipment) {
            this.equipment = equipment;
        }
        
        public Integer getEstimatedCaloriesBurn() {
            return estimatedCaloriesBurn;
        }
        
        public void setEstimatedCaloriesBurn(Integer estimatedCaloriesBurn) {
            this.estimatedCaloriesBurn = estimatedCaloriesBurn;
        }
    }
    
    public static class WorkoutPlan {
        private List<WorkoutDay> workoutDays;
        private String planName;
        private String planDescription;
        private Integer totalWeeks;
        private Integer workoutsPerWeek;
        
        public WorkoutPlan() {}
        
        public List<WorkoutDay> getWorkoutDays() {
            return workoutDays;
        }
        
        public void setWorkoutDays(List<WorkoutDay> workoutDays) {
            this.workoutDays = workoutDays;
        }
        
        public String getPlanName() {
            return planName;
        }
        
        public void setPlanName(String planName) {
            this.planName = planName;
        }
        
        public String getPlanDescription() {
            return planDescription;
        }
        
        public void setPlanDescription(String planDescription) {
            this.planDescription = planDescription;
        }
        
        public Integer getTotalWeeks() {
            return totalWeeks;
        }
        
        public void setTotalWeeks(Integer totalWeeks) {
            this.totalWeeks = totalWeeks;
        }
        
        public Integer getWorkoutsPerWeek() {
            return workoutsPerWeek;
        }
        
        public void setWorkoutsPerWeek(Integer workoutsPerWeek) {
            this.workoutsPerWeek = workoutsPerWeek;
        }
    }
    
    public static class WorkoutDay {
        private String dayName; // e.g., "Day 1: Upper Body"
        private String focus; // e.g., "Upper Body", "Lower Body", "Cardio"
        private List<Exercise> exercises;
        private Integer estimatedDuration; // in minutes
        
        public WorkoutDay() {}
        
        public String getDayName() {
            return dayName;
        }
        
        public void setDayName(String dayName) {
            this.dayName = dayName;
        }
        
        public String getFocus() {
            return focus;
        }
        
        public void setFocus(String focus) {
            this.focus = focus;
        }
        
        public List<Exercise> getExercises() {
            return exercises;
        }
        
        public void setExercises(List<Exercise> exercises) {
            this.exercises = exercises;
        }
        
        public Integer getEstimatedDuration() {
            return estimatedDuration;
        }
        
        public void setEstimatedDuration(Integer estimatedDuration) {
            this.estimatedDuration = estimatedDuration;
        }
    }
    
    public static class Exercise {
        private String name;
        private String description;
        private String sets;
        private String reps;
        private String weight; // e.g., "bodyweight", "moderate", "heavy"
        private String restTime; // e.g., "60 seconds"
        private List<String> targetMuscles;
        private String difficulty;
        private String instructions;
        private String videoUrl; // optional
        private String imageUrl; // optional
        
        public Exercise() {}
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getSets() {
            return sets;
        }
        
        public void setSets(String sets) {
            this.sets = sets;
        }
        
        public String getReps() {
            return reps;
        }
        
        public void setReps(String reps) {
            this.reps = reps;
        }
        
        public String getWeight() {
            return weight;
        }
        
        public void setWeight(String weight) {
            this.weight = weight;
        }
        
        public String getRestTime() {
            return restTime;
        }
        
        public void setRestTime(String restTime) {
            this.restTime = restTime;
        }
        
        public List<String> getTargetMuscles() {
            return targetMuscles;
        }
        
        public void setTargetMuscles(List<String> targetMuscles) {
            this.targetMuscles = targetMuscles;
        }
        
        public String getDifficulty() {
            return difficulty;
        }
        
        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }
        
        public String getInstructions() {
            return instructions;
        }
        
        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }
        
        public String getVideoUrl() {
            return videoUrl;
        }
        
        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
        
        public String getImageUrl() {
            return imageUrl;
        }
        
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
    
    public static class NutritionPlan {
        private String planName;
        private String description;
        private Integer dailyCalories;
        private MacroNutrients macros;
        private List<MealPlan> meals;
        private List<String> supplements;
        
        public NutritionPlan() {}
        
        public String getPlanName() {
            return planName;
        }
        
        public void setPlanName(String planName) {
            this.planName = planName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Integer getDailyCalories() {
            return dailyCalories;
        }
        
        public void setDailyCalories(Integer dailyCalories) {
            this.dailyCalories = dailyCalories;
        }
        
        public MacroNutrients getMacros() {
            return macros;
        }
        
        public void setMacros(MacroNutrients macros) {
            this.macros = macros;
        }
        
        public List<MealPlan> getMeals() {
            return meals;
        }
        
        public void setMeals(List<MealPlan> meals) {
            this.meals = meals;
        }
        
        public List<String> getSupplements() {
            return supplements;
        }
        
        public void setSupplements(List<String> supplements) {
            this.supplements = supplements;
        }
    }
    
    public static class MacroNutrients {
        private Integer protein; // in grams
        private Integer carbs; // in grams
        private Integer fats; // in grams
        
        public MacroNutrients() {}
        
        public Integer getProtein() {
            return protein;
        }
        
        public void setProtein(Integer protein) {
            this.protein = protein;
        }
        
        public Integer getCarbs() {
            return carbs;
        }
        
        public void setCarbs(Integer carbs) {
            this.carbs = carbs;
        }
        
        public Integer getFats() {
            return fats;
        }
        
        public void setFats(Integer fats) {
            this.fats = fats;
        }
    }
    
    public static class MealPlan {
        private String mealName; // e.g., "Breakfast", "Lunch"
        private String description;
        private Integer calories;
        private List<String> foods;
        
        public MealPlan() {}
        
        public String getMealName() {
            return mealName;
        }
        
        public void setMealName(String mealName) {
            this.mealName = mealName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Integer getCalories() {
            return calories;
        }
        
        public void setCalories(Integer calories) {
            this.calories = calories;
        }
        
        public List<String> getFoods() {
            return foods;
        }
        
        public void setFoods(List<String> foods) {
            this.foods = foods;
        }
    }
}
package com.pulserise.pulserisebackend.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

public class RecommendationRequest {
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    private String category;
    private List<String> preferences;
    private String context;
    private Integer limit;
    
    // Fitness profile data
    private String fitnessLevel; // beginner, intermediate, advanced
    private List<String> fitnessGoals; // fat loss, strength, endurance, muscle gain, etc.
    
    @Min(value = 30, message = "Weight must be at least 30 kg")
    @Max(value = 300, message = "Weight must be at most 300 kg")
    private Double weight; // in kg
    
    @Min(value = 100, message = "Height must be at least 100 cm")
    @Max(value = 250, message = "Height must be at most 250 cm")
    private Double height; // in cm
    
    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;
    
    private Double bmi; // calculated or provided
    
    // Additional fitness data
    private String activityLevel; // sedentary, lightly active, moderately active, very active
    private List<String> availableEquipment; // gym, dumbbells, resistance bands, etc.
    private Integer workoutDaysPerWeek;
    private Integer workoutDurationMinutes;
    private List<String> injuries; // any current injuries or limitations
    private List<String> preferredWorkoutTypes; // cardio, strength, yoga, etc.
    
    public RecommendationRequest() {}
    
    public RecommendationRequest(Long userId, String category, List<String> preferences) {
        this.userId = userId;
        this.category = category;
        this.preferences = preferences;
    }
    
    // Existing getters and setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public List<String> getPreferences() {
        return preferences;
    }
    
    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    // New fitness profile getters and setters
    public String getFitnessLevel() {
        return fitnessLevel;
    }
    
    public void setFitnessLevel(String fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }
    
    public List<String> getFitnessGoals() {
        return fitnessGoals;
    }
    
    public void setFitnessGoals(List<String> fitnessGoals) {
        this.fitnessGoals = fitnessGoals;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Double getBmi() {
        return bmi;
    }
    
    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }
    
    public String getActivityLevel() {
        return activityLevel;
    }
    
    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }
    
    public List<String> getAvailableEquipment() {
        return availableEquipment;
    }
    
    public void setAvailableEquipment(List<String> availableEquipment) {
        this.availableEquipment = availableEquipment;
    }
    
    public Integer getWorkoutDaysPerWeek() {
        return workoutDaysPerWeek;
    }
    
    public void setWorkoutDaysPerWeek(Integer workoutDaysPerWeek) {
        this.workoutDaysPerWeek = workoutDaysPerWeek;
    }
    
    public Integer getWorkoutDurationMinutes() {
        return workoutDurationMinutes;
    }
    
    public void setWorkoutDurationMinutes(Integer workoutDurationMinutes) {
        this.workoutDurationMinutes = workoutDurationMinutes;
    }
    
    public List<String> getInjuries() {
        return injuries;
    }
    
    public void setInjuries(List<String> injuries) {
        this.injuries = injuries;
    }
    
    public List<String> getPreferredWorkoutTypes() {
        return preferredWorkoutTypes;
    }
    
    public void setPreferredWorkoutTypes(List<String> preferredWorkoutTypes) {
        this.preferredWorkoutTypes = preferredWorkoutTypes;
    }
}
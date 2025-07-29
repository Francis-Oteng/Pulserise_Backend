package com.pulserise.pulserisebackend.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class WorkoutAnalyticsResponse {
    private WeeklyComparison weeklyComparison;
    private MuscleGroupAnalysis muscleGroupAnalysis;
    private WorkoutTrends trends;
    private List<PersonalizedInsight> insights;
    private List<WorkoutHistoryItem> workoutHistory;
    private WorkoutStats overallStats;

    // Constructors
    public WorkoutAnalyticsResponse() {}

    // Getters and Setters
    public WeeklyComparison getWeeklyComparison() {
        return weeklyComparison;
    }

    public void setWeeklyComparison(WeeklyComparison weeklyComparison) {
        this.weeklyComparison = weeklyComparison;
    }

    public MuscleGroupAnalysis getMuscleGroupAnalysis() {
        return muscleGroupAnalysis;
    }

    public void setMuscleGroupAnalysis(MuscleGroupAnalysis muscleGroupAnalysis) {
        this.muscleGroupAnalysis = muscleGroupAnalysis;
    }

    public WorkoutTrends getTrends() {
        return trends;
    }

    public void setTrends(WorkoutTrends trends) {
        this.trends = trends;
    }

    public List<PersonalizedInsight> getInsights() {
        return insights;
    }

    public void setInsights(List<PersonalizedInsight> insights) {
        this.insights = insights;
    }

    public List<WorkoutHistoryItem> getWorkoutHistory() {
        return workoutHistory;
    }

    public void setWorkoutHistory(List<WorkoutHistoryItem> workoutHistory) {
        this.workoutHistory = workoutHistory;
    }

    public WorkoutStats getOverallStats() {
        return overallStats;
    }

    public void setOverallStats(WorkoutStats overallStats) {
        this.overallStats = overallStats;
    }

    // Inner classes
    public static class WeeklyComparison {
        private Double thisWeekVolume;
        private Double lastWeekVolume;
        private Double volumeChangePercentage;
        private Integer thisWeekWorkouts;
        private Integer lastWeekWorkouts;
        private Integer workoutChangeCount;
        private String trend; // "increasing", "decreasing", "stable"

        // Constructors
        public WeeklyComparison() {}

        public WeeklyComparison(Double thisWeekVolume, Double lastWeekVolume, Integer thisWeekWorkouts, Integer lastWeekWorkouts) {
            this.thisWeekVolume = thisWeekVolume;
            this.lastWeekVolume = lastWeekVolume;
            this.thisWeekWorkouts = thisWeekWorkouts;
            this.lastWeekWorkouts = lastWeekWorkouts;
            this.workoutChangeCount = thisWeekWorkouts - lastWeekWorkouts;
            
            if (lastWeekVolume > 0) {
                this.volumeChangePercentage = ((thisWeekVolume - lastWeekVolume) / lastWeekVolume) * 100;
            } else {
                this.volumeChangePercentage = thisWeekVolume > 0 ? 100.0 : 0.0;
            }
            
            if (volumeChangePercentage > 5) {
                this.trend = "increasing";
            } else if (volumeChangePercentage < -5) {
                this.trend = "decreasing";
            } else {
                this.trend = "stable";
            }
        }

        // Getters and Setters
        public Double getThisWeekVolume() { return thisWeekVolume; }
        public void setThisWeekVolume(Double thisWeekVolume) { this.thisWeekVolume = thisWeekVolume; }
        public Double getLastWeekVolume() { return lastWeekVolume; }
        public void setLastWeekVolume(Double lastWeekVolume) { this.lastWeekVolume = lastWeekVolume; }
        public Double getVolumeChangePercentage() { return volumeChangePercentage; }
        public void setVolumeChangePercentage(Double volumeChangePercentage) { this.volumeChangePercentage = volumeChangePercentage; }
        public Integer getThisWeekWorkouts() { return thisWeekWorkouts; }
        public void setThisWeekWorkouts(Integer thisWeekWorkouts) { this.thisWeekWorkouts = thisWeekWorkouts; }
        public Integer getLastWeekWorkouts() { return lastWeekWorkouts; }
        public void setLastWeekWorkouts(Integer lastWeekWorkouts) { this.lastWeekWorkouts = lastWeekWorkouts; }
        public Integer getWorkoutChangeCount() { return workoutChangeCount; }
        public void setWorkoutChangeCount(Integer workoutChangeCount) { this.workoutChangeCount = workoutChangeCount; }
        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
    }

    public static class MuscleGroupAnalysis {
        private Map<String, MuscleGroupStats> muscleGroups;
        private List<String> strongestMuscleGroups;
        private List<String> weakestMuscleGroups;
        private List<String> neglectedMuscleGroups;

        // Constructors
        public MuscleGroupAnalysis() {}

        // Getters and Setters
        public Map<String, MuscleGroupStats> getMuscleGroups() { return muscleGroups; }
        public void setMuscleGroups(Map<String, MuscleGroupStats> muscleGroups) { this.muscleGroups = muscleGroups; }
        public List<String> getStrongestMuscleGroups() { return strongestMuscleGroups; }
        public void setStrongestMuscleGroups(List<String> strongestMuscleGroups) { this.strongestMuscleGroups = strongestMuscleGroups; }
        public List<String> getWeakestMuscleGroups() { return weakestMuscleGroups; }
        public void setWeakestMuscleGroups(List<String> weakestMuscleGroups) { this.weakestMuscleGroups = weakestMuscleGroups; }
        public List<String> getNeglectedMuscleGroups() { return neglectedMuscleGroups; }
        public void setNeglectedMuscleGroups(List<String> neglectedMuscleGroups) { this.neglectedMuscleGroups = neglectedMuscleGroups; }
    }

    public static class MuscleGroupStats {
        private String muscleGroup;
        private Double totalVolume;
        private Integer workoutCount;
        private Double averageVolume;
        private Integer score; // out of 100
        private String status; // "strong", "average", "weak", "neglected"

        // Constructors
        public MuscleGroupStats() {}

        public MuscleGroupStats(String muscleGroup, Double totalVolume, Integer workoutCount) {
            this.muscleGroup = muscleGroup;
            this.totalVolume = totalVolume;
            this.workoutCount = workoutCount;
            this.averageVolume = workoutCount > 0 ? totalVolume / workoutCount : 0.0;
        }

        // Getters and Setters
        public String getMuscleGroup() { return muscleGroup; }
        public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }
        public Double getTotalVolume() { return totalVolume; }
        public void setTotalVolume(Double totalVolume) { this.totalVolume = totalVolume; }
        public Integer getWorkoutCount() { return workoutCount; }
        public void setWorkoutCount(Integer workoutCount) { this.workoutCount = workoutCount; }
        public Double getAverageVolume() { return averageVolume; }
        public void setAverageVolume(Double averageVolume) { this.averageVolume = averageVolume; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class WorkoutTrends {
        private List<VolumeDataPoint> volumeOverTime;
        private List<FrequencyDataPoint> frequencyOverTime;
        private Map<String, Integer> workoutTypeDistribution;
        private Double averageWorkoutDuration;

        // Constructors
        public WorkoutTrends() {}

        // Getters and Setters
        public List<VolumeDataPoint> getVolumeOverTime() { return volumeOverTime; }
        public void setVolumeOverTime(List<VolumeDataPoint> volumeOverTime) { this.volumeOverTime = volumeOverTime; }
        public List<FrequencyDataPoint> getFrequencyOverTime() { return frequencyOverTime; }
        public void setFrequencyOverTime(List<FrequencyDataPoint> frequencyOverTime) { this.frequencyOverTime = frequencyOverTime; }
        public Map<String, Integer> getWorkoutTypeDistribution() { return workoutTypeDistribution; }
        public void setWorkoutTypeDistribution(Map<String, Integer> workoutTypeDistribution) { this.workoutTypeDistribution = workoutTypeDistribution; }
        public Double getAverageWorkoutDuration() { return averageWorkoutDuration; }
        public void setAverageWorkoutDuration(Double averageWorkoutDuration) { this.averageWorkoutDuration = averageWorkoutDuration; }
    }

    public static class VolumeDataPoint {
        private LocalDateTime date;
        private Double volume;

        public VolumeDataPoint() {}
        public VolumeDataPoint(LocalDateTime date, Double volume) {
            this.date = date;
            this.volume = volume;
        }

        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public Double getVolume() { return volume; }
        public void setVolume(Double volume) { this.volume = volume; }
    }

    public static class FrequencyDataPoint {
        private LocalDateTime date;
        private Integer workoutCount;

        public FrequencyDataPoint() {}
        public FrequencyDataPoint(LocalDateTime date, Integer workoutCount) {
            this.date = date;
            this.workoutCount = workoutCount;
        }

        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public Integer getWorkoutCount() { return workoutCount; }
        public void setWorkoutCount(Integer workoutCount) { this.workoutCount = workoutCount; }
    }

    public static class PersonalizedInsight {
        private String type; // "achievement", "warning", "suggestion", "milestone"
        private String title;
        private String message;
        private String icon;
        private Integer priority; // 1-5, 5 being highest

        // Constructors
        public PersonalizedInsight() {}

        public PersonalizedInsight(String type, String title, String message, String icon, Integer priority) {
            this.type = type;
            this.title = title;
            this.message = message;
            this.icon = icon;
            this.priority = priority;
        }

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
    }

    public static class WorkoutHistoryItem {
        private LocalDateTime date;
        private String workoutName;
        private String workoutType;
        private Integer durationMinutes;
        private Double totalVolume;
        private Integer exerciseCount;
        private Boolean completed;

        // Constructors
        public WorkoutHistoryItem() {}

        // Getters and Setters
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public String getWorkoutName() { return workoutName; }
        public void setWorkoutName(String workoutName) { this.workoutName = workoutName; }
        public String getWorkoutType() { return workoutType; }
        public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
        public Double getTotalVolume() { return totalVolume; }
        public void setTotalVolume(Double totalVolume) { this.totalVolume = totalVolume; }
        public Integer getExerciseCount() { return exerciseCount; }
        public void setExerciseCount(Integer exerciseCount) { this.exerciseCount = exerciseCount; }
        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
    }

    public static class WorkoutStats {
        private Integer totalWorkouts;
        private Double totalVolume;
        private Integer totalExercises;
        private Double averageWorkoutDuration;
        private Integer currentStreak;
        private Integer longestStreak;

        // Constructors
        public WorkoutStats() {}

        // Getters and Setters
        public Integer getTotalWorkouts() { return totalWorkouts; }
        public void setTotalWorkouts(Integer totalWorkouts) { this.totalWorkouts = totalWorkouts; }
        public Double getTotalVolume() { return totalVolume; }
        public void setTotalVolume(Double totalVolume) { this.totalVolume = totalVolume; }
        public Integer getTotalExercises() { return totalExercises; }
        public void setTotalExercises(Integer totalExercises) { this.totalExercises = totalExercises; }
        public Double getAverageWorkoutDuration() { return averageWorkoutDuration; }
        public void setAverageWorkoutDuration(Double averageWorkoutDuration) { this.averageWorkoutDuration = averageWorkoutDuration; }
        public Integer getCurrentStreak() { return currentStreak; }
        public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }
        public Integer getLongestStreak() { return longestStreak; }
        public void setLongestStreak(Integer longestStreak) { this.longestStreak = longestStreak; }
    }
}
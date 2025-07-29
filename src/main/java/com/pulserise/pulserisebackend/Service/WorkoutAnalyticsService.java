package com.pulserise.pulserisebackend.Service;

import com.pulserise.pulserisebackend.Dto.WorkoutAnalyticsResponse;
import com.pulserise.pulserisebackend.Model.User;
import com.pulserise.pulserisebackend.Model.Workout;
import com.pulserise.pulserisebackend.Repository.WorkoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkoutAnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkoutAnalyticsService.class);
    
    @Autowired
    private WorkoutRepository workoutRepository;
    
    public WorkoutAnalyticsResponse getComprehensiveAnalytics(User user, String period) {
        logger.info("Generating comprehensive analytics for user: {} with period: {}", user.getUsername(), period);
        
        WorkoutAnalyticsResponse response = new WorkoutAnalyticsResponse();
        
        // Set date ranges based on period
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDateForPeriod(period, endDate);
        
        try {
            // Generate all analytics components
            response.setWeeklyComparison(generateWeeklyComparison(user));
            response.setMuscleGroupAnalysis(generateMuscleGroupAnalysis(user, startDate, endDate));
            response.setTrends(generateWorkoutTrends(user, startDate, endDate));
            response.setInsights(generatePersonalizedInsights(user, startDate, endDate));
            response.setWorkoutHistory(generateWorkoutHistory(user, startDate, endDate));
            response.setOverallStats(generateOverallStats(user));
            
            logger.info("Successfully generated analytics for user: {}", user.getUsername());
            return response;
            
        } catch (Exception e) {
            logger.error("Error generating analytics for user: {}", user.getUsername(), e);
            return createFallbackAnalytics();
        }
    }
    
    private WorkoutAnalyticsResponse.WeeklyComparison generateWeeklyComparison(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisWeekStart = now.minusDays(7);
        LocalDateTime lastWeekStart = now.minusDays(14);
        LocalDateTime lastWeekEnd = now.minusDays(7);
        
        // Get this week's data
        Double thisWeekVolume = workoutRepository.getTotalVolumeByUserAndDateRange(user, thisWeekStart, now);
        Long thisWeekWorkoutsLong = workoutRepository.countCompletedWorkoutsByUserAndDateRange(user, thisWeekStart, now);
        
        // Get last week's data
        Double lastWeekVolume = workoutRepository.getTotalVolumeByUserAndDateRange(user, lastWeekStart, lastWeekEnd);
        Long lastWeekWorkoutsLong = workoutRepository.countCompletedWorkoutsByUserAndDateRange(user, lastWeekStart, lastWeekEnd);
        
        // Convert Long to Integer safely
        Integer thisWeekWorkouts = thisWeekWorkoutsLong != null ? thisWeekWorkoutsLong.intValue() : 0;
        Integer lastWeekWorkouts = lastWeekWorkoutsLong != null ? lastWeekWorkoutsLong.intValue() : 0;
        
        // Handle null values
        thisWeekVolume = thisWeekVolume != null ? thisWeekVolume : 0.0;
        lastWeekVolume = lastWeekVolume != null ? lastWeekVolume : 0.0;
        
        return new WorkoutAnalyticsResponse.WeeklyComparison(thisWeekVolume, lastWeekVolume, thisWeekWorkouts, lastWeekWorkouts);
    }
    
    private WorkoutAnalyticsResponse.MuscleGroupAnalysis generateMuscleGroupAnalysis(User user, LocalDateTime startDate, LocalDateTime endDate) {
        WorkoutAnalyticsResponse.MuscleGroupAnalysis analysis = new WorkoutAnalyticsResponse.MuscleGroupAnalysis();
        
        // Get volume by muscle group
        List<Object[]> volumeData = workoutRepository.getVolumeByWorkoutType(user, startDate, endDate);
        List<Object[]> workoutCountData = workoutRepository.getWorkoutTypeDistribution(user, startDate, endDate);
        
        Map<String, WorkoutAnalyticsResponse.MuscleGroupStats> muscleGroupStats = new HashMap<>();
        
        // Process volume data
        for (Object[] row : volumeData) {
            String muscleGroup = (String) row[0];
            Double volume = ((Number) row[1]).doubleValue();
            
            WorkoutAnalyticsResponse.MuscleGroupStats stats = muscleGroupStats.getOrDefault(muscleGroup, 
                new WorkoutAnalyticsResponse.MuscleGroupStats(muscleGroup, 0.0, 0));
            stats.setTotalVolume(volume);
            muscleGroupStats.put(muscleGroup, stats);
        }
        
        // Process workout count data
        for (Object[] row : workoutCountData) {
            String muscleGroup = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            
            WorkoutAnalyticsResponse.MuscleGroupStats stats = muscleGroupStats.getOrDefault(muscleGroup, 
                new WorkoutAnalyticsResponse.MuscleGroupStats(muscleGroup, 0.0, 0));
            stats.setWorkoutCount(count);
            stats.setAverageVolume(count > 0 ? stats.getTotalVolume() / count : 0.0);
            muscleGroupStats.put(muscleGroup, stats);
        }
        
        // Calculate scores and status for each muscle group
        calculateMuscleGroupScores(muscleGroupStats);
        
        analysis.setMuscleGroups(muscleGroupStats);
        analysis.setStrongestMuscleGroups(getTopMuscleGroups(muscleGroupStats, true));
        analysis.setWeakestMuscleGroups(getTopMuscleGroups(muscleGroupStats, false));
        analysis.setNeglectedMuscleGroups(getNeglectedMuscleGroups(muscleGroupStats));
        
        return analysis;
    }
    
    private WorkoutAnalyticsResponse.WorkoutTrends generateWorkoutTrends(User user, LocalDateTime startDate, LocalDateTime endDate) {
        WorkoutAnalyticsResponse.WorkoutTrends trends = new WorkoutAnalyticsResponse.WorkoutTrends();
        
        // Generate volume over time (weekly data points)
        List<WorkoutAnalyticsResponse.VolumeDataPoint> volumeData = new ArrayList<>();
        LocalDateTime current = startDate;
        while (current.isBefore(endDate)) {
            LocalDateTime weekEnd = current.plusWeeks(1);
            if (weekEnd.isAfter(endDate)) weekEnd = endDate;
            
            Double weeklyVolume = workoutRepository.getTotalVolumeByUserAndDateRange(user, current, weekEnd);
            volumeData.add(new WorkoutAnalyticsResponse.VolumeDataPoint(current, weeklyVolume != null ? weeklyVolume : 0.0));
            
            current = current.plusWeeks(1);
        }
        trends.setVolumeOverTime(volumeData);
        
        // Generate frequency over time
        List<WorkoutAnalyticsResponse.FrequencyDataPoint> frequencyData = new ArrayList<>();
        current = startDate;
        while (current.isBefore(endDate)) {
            LocalDateTime weekEnd = current.plusWeeks(1);
            if (weekEnd.isAfter(endDate)) weekEnd = endDate;
            
            Long weeklyCount = workoutRepository.countCompletedWorkoutsByUserAndDateRange(user, current, weekEnd);
            frequencyData.add(new WorkoutAnalyticsResponse.FrequencyDataPoint(current, weeklyCount != null ? weeklyCount.intValue() : 0));
            
            current = current.plusWeeks(1);
        }
        trends.setFrequencyOverTime(frequencyData);
        
        // Get workout type distribution
        List<Object[]> typeDistribution = workoutRepository.getWorkoutTypeDistribution(user, startDate, endDate);
        Map<String, Integer> distributionMap = new HashMap<>();
        for (Object[] row : typeDistribution) {
            distributionMap.put((String) row[0], ((Number) row[1]).intValue());
        }
        trends.setWorkoutTypeDistribution(distributionMap);
        
        // Get average workout duration
        Double avgDuration = workoutRepository.getAverageWorkoutDuration(user, startDate, endDate);
        trends.setAverageWorkoutDuration(avgDuration != null ? avgDuration : 0.0);
        
        return trends;
    }
    
    private List<WorkoutAnalyticsResponse.PersonalizedInsight> generatePersonalizedInsights(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<WorkoutAnalyticsResponse.PersonalizedInsight> insights = new ArrayList<>();
        
        // Weekly comparison insight
        WorkoutAnalyticsResponse.WeeklyComparison comparison = generateWeeklyComparison(user);
        if (comparison.getVolumeChangePercentage() > 15) {
            insights.add(new WorkoutAnalyticsResponse.PersonalizedInsight(
                "achievement",
                "Volume Increase!",
                String.format("Your strength training volume increased by %.1f%% this week. Great progress!", comparison.getVolumeChangePercentage()),
                "💪",
                5
            ));
        } else if (comparison.getVolumeChangePercentage() < -15) {
            insights.add(new WorkoutAnalyticsResponse.PersonalizedInsight(
                "warning",
                "Volume Decrease",
                String.format("Your training volume decreased by %.1f%% this week. Consider increasing intensity.", Math.abs(comparison.getVolumeChangePercentage())),
                "⚠️",
                4
            ));
        }
        
        // Workout frequency insight
        Long totalWorkouts = workoutRepository.countCompletedWorkoutsByUserAndDateRange(user, startDate, endDate);
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        double workoutsPerWeek = totalWorkouts != null ? (totalWorkouts * 7.0) / daysBetween : 0;
        
        if (workoutsPerWeek < 2) {
            insights.add(new WorkoutAnalyticsResponse.PersonalizedInsight(
                "suggestion",
                "Increase Frequency",
                String.format("You averaged %.1f workouts per week. Try to aim for 3-4 sessions for optimal results.", workoutsPerWeek),
                "📅",
                3
            ));
        }
        
        // Muscle group balance insight
        List<Object[]> muscleGroupData = workoutRepository.getWorkoutTypeDistribution(user, startDate, endDate);

        if (muscleGroupData.size() < 3) {
            insights.add(new WorkoutAnalyticsResponse.PersonalizedInsight(
                "suggestion",
                "Diversify Training",
                "You're focusing on limited muscle groups. Consider adding more variety to your workouts.",
                "🎯",
                3
            ));
        }
        
        // Consistency insight
        List<Object[]> frequencyByDay = workoutRepository.getWorkoutFrequencyByDayOfWeek(user, startDate, endDate);
        if (frequencyByDay.size() >= 3) {
            insights.add(new WorkoutAnalyticsResponse.PersonalizedInsight(
                "achievement",
                "Great Consistency!",
                "You're maintaining a consistent workout schedule throughout the week. Keep it up!",
                "🔥",
                4
            ));
        }
        
        return insights.stream()
                .sorted((a, b) -> b.getPriority().compareTo(a.getPriority()))
                .collect(Collectors.toList());
    }
    
    private List<WorkoutAnalyticsResponse.WorkoutHistoryItem> generateWorkoutHistory(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Workout> workouts = workoutRepository.findByUserAndStartTimeBetweenOrderByStartTimeDesc(user, startDate, endDate);
        
        return workouts.stream().map(workout -> {
            WorkoutAnalyticsResponse.WorkoutHistoryItem item = new WorkoutAnalyticsResponse.WorkoutHistoryItem();
            item.setDate(workout.getStartTime());
            item.setWorkoutName(workout.getWorkoutName());
            item.setWorkoutType(workout.getWorkoutType());
            item.setDurationMinutes(workout.getDurationMinutes());
            item.setTotalVolume(workout.getTotalVolume());
            item.setExerciseCount(workout.getExercises() != null ? workout.getExercises().size() : 0);
            item.setCompleted(workout.getCompleted());
            return item;
        }).collect(Collectors.toList());
    }
    
    private WorkoutAnalyticsResponse.WorkoutStats generateOverallStats(User user) {
        WorkoutAnalyticsResponse.WorkoutStats stats = new WorkoutAnalyticsResponse.WorkoutStats();
        
        LocalDateTime allTimeStart = LocalDateTime.now().minusYears(2); // Get 2 years of data
        LocalDateTime now = LocalDateTime.now();
        
        Long totalWorkouts = workoutRepository.countCompletedWorkoutsByUserAndDateRange(user, allTimeStart, now);
        Double totalVolume = workoutRepository.getTotalVolumeByUserAndDateRange(user, allTimeStart, now);
        Double avgDuration = workoutRepository.getAverageWorkoutDuration(user, allTimeStart, now);
        
        stats.setTotalWorkouts(totalWorkouts != null ? totalWorkouts.intValue() : 0);
        stats.setTotalVolume(totalVolume != null ? totalVolume : 0.0);
        stats.setAverageWorkoutDuration(avgDuration != null ? avgDuration : 0.0);
        
        // Calculate streaks
        calculateWorkoutStreaks(user, stats);
        
        return stats;
    }
    
    // Helper methods
    private LocalDateTime getStartDateForPeriod(String period, LocalDateTime endDate) {
        switch (period.toLowerCase()) {
            case "week":
                return endDate.minusWeeks(1);
            case "month":
                return endDate.minusMonths(1);
            case "year":
                return endDate.minusYears(1);
            case "all":
                return endDate.minusYears(2);
            default:
                return endDate.minusMonths(1);
        }
    }
    
    private void calculateMuscleGroupScores(Map<String, WorkoutAnalyticsResponse.MuscleGroupStats> muscleGroupStats) {
        if (muscleGroupStats.isEmpty()) return;
        
        // Find max volume for normalization
        double maxVolume = muscleGroupStats.values().stream()
                .mapToDouble(WorkoutAnalyticsResponse.MuscleGroupStats::getTotalVolume)
                .max().orElse(1.0);
        
        for (WorkoutAnalyticsResponse.MuscleGroupStats stats : muscleGroupStats.values()) {
            // Calculate score based on volume and frequency
            double volumeScore = (stats.getTotalVolume() / maxVolume) * 60; // 60% weight for volume
            double frequencyScore = Math.min(stats.getWorkoutCount() * 10, 40); // 40% weight for frequency, cap at 40
            
            int totalScore = (int) Math.round(volumeScore + frequencyScore);
            stats.setScore(Math.min(totalScore, 100));
            
            // Set status based on score
            if (totalScore >= 80) {
                stats.setStatus("strong");
            } else if (totalScore >= 50) {
                stats.setStatus("average");
            } else if (totalScore >= 20) {
                stats.setStatus("weak");
            } else {
                stats.setStatus("neglected");
            }
        }
    }
    
    private List<String> getTopMuscleGroups(Map<String, WorkoutAnalyticsResponse.MuscleGroupStats> muscleGroupStats, boolean strongest) {
        return muscleGroupStats.values().stream()
                .sorted((a, b) -> strongest ? b.getScore().compareTo(a.getScore()) : a.getScore().compareTo(b.getScore()))
                .limit(3)
                .map(WorkoutAnalyticsResponse.MuscleGroupStats::getMuscleGroup)
                .collect(Collectors.toList());
    }
    
    private List<String> getNeglectedMuscleGroups(Map<String, WorkoutAnalyticsResponse.MuscleGroupStats> muscleGroupStats) {
        return muscleGroupStats.values().stream()
                .filter(stats -> "neglected".equals(stats.getStatus()))
                .map(WorkoutAnalyticsResponse.MuscleGroupStats::getMuscleGroup)
                .collect(Collectors.toList());
    }
    
    private void calculateWorkoutStreaks(User user, WorkoutAnalyticsResponse.WorkoutStats stats) {
        // Get recent workouts to calculate current streak
        List<Workout> recentWorkouts = workoutRepository.findByUserAndStartTimeAfterOrderByStartTimeDesc(user, LocalDateTime.now().minusDays(30));        
        // Simple streak calculation (can be enhanced)
        int currentStreak = 0;
        int longestStreak = 0;
        int tempStreak = 0;
        
        LocalDateTime lastWorkoutDate = null;
        for (Workout workout : recentWorkouts) {
            if (workout.getCompleted()) {
                if (lastWorkoutDate == null || ChronoUnit.DAYS.between(workout.getStartTime(), lastWorkoutDate) <= 2) {
                    tempStreak++;
                    if (lastWorkoutDate == null) currentStreak = tempStreak;
                } else {
                    longestStreak = Math.max(longestStreak, tempStreak);
                    tempStreak = 1;
                    currentStreak = 0; // Reset current streak if gap is too large
                }
                lastWorkoutDate = workout.getStartTime();
            }
        }
        
        longestStreak = Math.max(longestStreak, tempStreak);
        
        stats.setCurrentStreak(currentStreak);
        stats.setLongestStreak(longestStreak);
    }
    
    private WorkoutAnalyticsResponse createFallbackAnalytics() {
        WorkoutAnalyticsResponse response = new WorkoutAnalyticsResponse();
        
        // Create empty/default analytics
        response.setWeeklyComparison(new WorkoutAnalyticsResponse.WeeklyComparison(0.0, 0.0, 0, 0));
        response.setMuscleGroupAnalysis(new WorkoutAnalyticsResponse.MuscleGroupAnalysis());
        response.setTrends(new WorkoutAnalyticsResponse.WorkoutTrends());
        response.setInsights(Arrays.asList(
            new WorkoutAnalyticsResponse.PersonalizedInsight(
                "suggestion",
                "Start Your Journey",
                "Begin logging your workouts to see detailed analytics and insights!",
                "🚀",
                5
            )
        ));
        response.setWorkoutHistory(new ArrayList<>());
        response.setOverallStats(new WorkoutAnalyticsResponse.WorkoutStats());
        
        return response;
    }
}
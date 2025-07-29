package com.pulserise.pulserisebackend.Repository;

import com.pulserise.pulserisebackend.Model.Workout;
import com.pulserise.pulserisebackend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    
    // Find workouts by user
    List<Workout> findByUserOrderByStartTimeDesc(User user);
    
    // Find workouts by user and date range
    List<Workout> findByUserAndStartTimeBetweenOrderByStartTimeDesc(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find workouts by user and workout type
    List<Workout> findByUserAndWorkoutTypeOrderByStartTimeDesc(User user, String workoutType);
    
    // Find completed workouts by user
    List<Workout> findByUserAndCompletedTrueOrderByStartTimeDesc(User user);
    
    // Find workouts by user in the last N days
    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.startTime >= :startDate ORDER BY w.startTime DESC")
    List<Workout> findByUserAndStartTimeAfter(@Param("user") User user, @Param("startDate") LocalDateTime startDate);
    
    // Get workout count by user and date range
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user = :user AND w.startTime BETWEEN :startDate AND :endDate AND w.completed = true")
    Long countCompletedWorkoutsByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get total volume by user and date range
    @Query("SELECT COALESCE(SUM(w.totalVolume), 0) FROM Workout w WHERE w.user = :user AND w.startTime BETWEEN :startDate AND :endDate AND w.completed = true")
    Double getTotalVolumeByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get workout count by muscle group and date range
    @Query("SELECT e.muscleGroup, COUNT(DISTINCT w) FROM Workout w JOIN w.exercises e WHERE w.user = :user AND w.startTime BETWEEN :startDate AND :endDate AND w.completed = true GROUP BY e.muscleGroup")
    List<Object[]> getWorkoutCountByMuscleGroupAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get volume by muscle group and date range
    @Query("SELECT e.muscleGroup, COALESCE(SUM(s.weight * s.reps), 0) FROM Workout w JOIN w.exercises e JOIN e.sets s WHERE w.user = :user AND w.startTime BETWEEN :startDate AND :endDate AND w.completed = true AND s.completed = true GROUP BY e.muscleGroup")
    List<Object[]> getVolumeByMuscleGroupAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get workout type distribution
    @Query("SELECT w.workoutType, COUNT(w) FROM Workout w WHERE w.user = :user AND w.startTime BETWEEN :startDate AND :endDate AND w.completed = true GROUP BY w.workoutType")
    List<Object[]> getWorkoutTypeDistribution(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get average workout duration
    @Query("SELECT AVG(w.durationMinutes) FROM Workout w WHERE w.user = :user AND w.startTime BETWEEN :startDate AND :endDate AND w.completed = true AND w.durationMinutes IS NOT NULL")
    Double getAverageWorkoutDuration(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get workout frequency by day of week
    @Query("SELECT EXTRACT(DOW FROM w.startTime), COUNT(w) FROM Workout w WHERE w.user = :user AND w.startTime BETWEEN :startDate AND :endDate AND w.completed = true GROUP BY EXTRACT(DOW FROM w.startTime) ORDER BY EXTRACT(DOW FROM w.startTime)")
    List<Object[]> getWorkoutFrequencyByDayOfWeek(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
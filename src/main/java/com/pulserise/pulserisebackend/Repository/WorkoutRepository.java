package com.pulserise.pulserisebackend.Repository;

import com.pulserise.pulserisebackend.Model.Workout;
import com.pulserise.pulserisebackend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    
    // Find incomplete workouts by user
    List<Workout> findByUserAndCompletedFalseOrderByStartTimeDesc(User user);
    
    // Find workouts by user in the last N days
    List<Workout> findByUserAndStartTimeAfterOrderByStartTimeDesc(User user, LocalDateTime startDate);
    
    // Find workouts by user and workout name containing
    List<Workout> findByUserAndWorkoutNameContainingIgnoreCaseOrderByStartTimeDesc(User user, String workoutName);
    
    // Get workout count by user and date range
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.startTime BETWEEN :startDate AND :endDate")
    Long countCompletedWorkoutsByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get total completed workouts by user
    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user = :user AND w.completed = true")
    Long countCompletedWorkoutsByUser(@Param("user") User user);

    @Query("SELECT w.workoutType, COALESCE(SUM(w.totalVolume), 0.0) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.totalVolume IS NOT NULL AND w.startTime BETWEEN :startDate AND :endDate GROUP BY w.workoutType ORDER BY SUM(w.totalVolume) DESC")
    List<Object[]> getVolumeByWorkoutType(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get total volume by user and date range
    @Query("SELECT COALESCE(SUM(w.totalVolume), 0.0) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.totalVolume IS NOT NULL AND w.startTime BETWEEN :startDate AND :endDate")
    Double getTotalVolumeByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get total calories burned by user and date range
    @Query("SELECT COALESCE(SUM(w.caloriesBurned), 0) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.caloriesBurned IS NOT NULL AND w.startTime BETWEEN :startDate AND :endDate")
    Integer getTotalCaloriesByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get workout type distribution
    @Query("SELECT w.workoutType, COUNT(w) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.startTime BETWEEN :startDate AND :endDate GROUP BY w.workoutType ORDER BY COUNT(w) DESC")
    List<Object[]> getWorkoutTypeDistribution(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get average workout duration
    @Query("SELECT AVG(w.durationMinutes) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.durationMinutes IS NOT NULL AND w.startTime BETWEEN :startDate AND :endDate")
    Double getAverageWorkoutDuration(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get workout frequency by day of week (0=Sunday, 1=Monday, etc. for PostgreSQL)
    @Query("SELECT EXTRACT(DOW FROM w.startTime) as dayOfWeek, COUNT(w) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.startTime BETWEEN :startDate AND :endDate GROUP BY EXTRACT(DOW FROM w.startTime) ORDER BY dayOfWeek")
    List<Object[]> getWorkoutFrequencyByDayOfWeek(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get workout frequency by month (PostgreSQL compatible)
    @Query("SELECT EXTRACT(YEAR FROM w.startTime) as year, EXTRACT(MONTH FROM w.startTime) as month, COUNT(w) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.startTime BETWEEN :startDate AND :endDate GROUP BY EXTRACT(YEAR FROM w.startTime), EXTRACT(MONTH FROM w.startTime) ORDER BY year, month")
    List<Object[]> getWorkoutFrequencyByMonth(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get recent workouts with limit
    @Query("SELECT w FROM Workout w WHERE w.user = :user ORDER BY w.startTime DESC LIMIT :limit")
    List<Workout> findRecentWorkoutsByUser(@Param("user") User user, @Param("limit") int limit);
    
    // Get workouts with highest volume
    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.completed = true AND w.totalVolume IS NOT NULL ORDER BY w.totalVolume DESC LIMIT :limit")
    List<Workout> findTopWorkoutsByVolume(@Param("user") User user, @Param("limit") int limit);
    
    // Get workouts with longest duration
    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.completed = true AND w.durationMinutes IS NOT NULL ORDER BY w.durationMinutes DESC LIMIT :limit")
    List<Workout> findTopWorkoutsByDuration(@Param("user") User user, @Param("limit") int limit);
    
    // Get total sets and reps by user and date range
    @Query("SELECT COALESCE(SUM(w.totalSets), 0), COALESCE(SUM(w.totalReps), 0) FROM Workout w WHERE w.user = :user AND w.completed = true AND w.startTime BETWEEN :startDate AND :endDate")
    Object[] getTotalSetsAndRepsByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get workout streak (consecutive days with workouts) - PostgreSQL compatible
    @Query(value = """
        WITH RECURSIVE workout_dates AS (
            SELECT DISTINCT DATE_TRUNC('day', start_time)::date as workout_date
            FROM workouts
            WHERE user_id = :userId AND completed = true
            ORDER BY workout_date DESC
        ),
        date_diffs AS (
            SELECT workout_date,
                   LAG(workout_date) OVER (ORDER BY workout_date DESC) as prev_date,
                   (LAG(workout_date) OVER (ORDER BY workout_date DESC) - workout_date) as day_diff
            FROM workout_dates
        ),
        streak_groups AS (
            SELECT workout_date,
                   SUM(CASE WHEN day_diff = 1 OR day_diff IS NULL THEN 0 ELSE 1 END)
                   OVER (ORDER BY workout_date DESC) as streak_group
            FROM date_diffs
        )
        SELECT COUNT(*) as current_streak
        FROM streak_groups 
        WHERE streak_group = 0
        """, nativeQuery = true)
    Integer getCurrentWorkoutStreak(@Param("userId") Long userId);
    
    // Find workouts by multiple workout types
    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.workoutType IN :workoutTypes ORDER BY w.startTime DESC")
    List<Workout> findByUserAndWorkoutTypeIn(@Param("user") User user, @Param("workoutTypes") List<String> workoutTypes);
    
    // Get monthly workout summary
    @Query("""
        SELECT 
            YEAR(w.startTime) as year,
            MONTH(w.startTime) as month,
            COUNT(w) as totalWorkouts,
            COALESCE(SUM(w.durationMinutes), 0) as totalDuration,
            COALESCE(SUM(w.totalVolume), 0.0) as totalVolume,
            COALESCE(SUM(w.caloriesBurned), 0) as totalCalories
        FROM Workout w 
        WHERE w.user = :user AND w.completed = true 
        AND w.startTime BETWEEN :startDate AND :endDate
        GROUP BY YEAR(w.startTime), MONTH(w.startTime)
        ORDER BY year DESC, month DESC
        """)
    List<Object[]> getMonthlyWorkoutSummary(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Check if user has workout on specific date (PostgreSQL compatible)
    @Query("SELECT COUNT(w) > 0 FROM Workout w WHERE w.user = :user AND w.completed = true AND DATE_TRUNC('day', w.startTime) = DATE_TRUNC('day', :date)")
    Boolean hasWorkoutOnDate(@Param("user") User user, @Param("date") LocalDateTime date);
    
    // Get workout by user and id
    Optional<Workout> findByIdAndUser(Long id, User user);
    
    // Delete workouts by user (for cleanup)
    void deleteByUser(User user);
    
    // Find workouts that are not completed and started more than X hours ago (for cleanup)
    @Query("SELECT w FROM Workout w WHERE w.user = :user AND w.completed = false AND w.startTime < :cutoffTime")
    List<Workout> findIncompleteWorkoutsOlderThan(@Param("user") User user, @Param("cutoffTime") LocalDateTime cutoffTime);
}
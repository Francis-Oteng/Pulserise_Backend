package com.pulserise.pulserisebackend.Repository;

import com.pulserise.pulserisebackend.Model.Exercise;
import com.pulserise.pulserisebackend.Model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    
    // Find exercises by workout
    List<Exercise> findByWorkoutOrderByCreatedAt(Workout workout);
    
    // Find exercises by muscle group
    List<Exercise> findByMuscleGroupOrderByCreatedAt(String muscleGroup);
    
    // Find exercises by workout and muscle group
    List<Exercise> findByWorkoutAndMuscleGroupOrderByCreatedAt(Workout workout, String muscleGroup);
    
    // Get exercise count by muscle group for a user
    @Query("SELECT e.muscleGroup, COUNT(e) FROM Exercise e JOIN e.workout w WHERE w.user.id = :userId GROUP BY e.muscleGroup")
    List<Object[]> getExerciseCountByMuscleGroup(@Param("userId") Long userId);
    
    // Get most performed exercises for a user
    @Query("SELECT e.exerciseName, COUNT(e) as exerciseCount FROM Exercise e JOIN e.workout w WHERE w.user.id = :userId GROUP BY e.exerciseName ORDER BY exerciseCount DESC")
    List<Object[]> getMostPerformedExercises(@Param("userId") Long userId);
}
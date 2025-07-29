package com.pulserise.pulserisebackend.Repository;

import com.pulserise.pulserisebackend.Model.ExerciseSet;
import com.pulserise.pulserisebackend.Model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {
    
    // Find sets by exercise
    List<ExerciseSet> findByExerciseOrderBySetNumber(Exercise exercise);
    
    // Find completed sets by exercise
    List<ExerciseSet> findByExerciseAndCompletedTrueOrderBySetNumber(Exercise exercise);
    
    // Get total volume for an exercise
    @Query("SELECT COALESCE(SUM(s.weight * s.reps), 0) FROM ExerciseSet s WHERE s.exercise = :exercise AND s.completed = true")
    Double getTotalVolumeByExercise(@Param("exercise") Exercise exercise);
    
    // Get max weight for an exercise
    @Query("SELECT MAX(s.weight) FROM ExerciseSet s WHERE s.exercise = :exercise AND s.completed = true")
    Double getMaxWeightByExercise(@Param("exercise") Exercise exercise);
    
    // Get total reps for an exercise
    @Query("SELECT COALESCE(SUM(s.reps), 0) FROM ExerciseSet s WHERE s.exercise = :exercise AND s.completed = true")
    Long getTotalRepsByExercise(@Param("exercise") Exercise exercise);
}
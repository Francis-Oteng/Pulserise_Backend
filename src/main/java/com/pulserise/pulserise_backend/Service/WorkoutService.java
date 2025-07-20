package com.pulserise.pulserise_backend.Service;

import com.pulserise.pulserise_backend.dto.WorkoutDto;
import com.pulserise.pulserise_backend.entities.Workout;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService {
    public Workout generateWorkout(WorkoutDto dto) {
        // Example logic, expand as needed
        String name = "Custom Workout";
        String description = "Workout for " + dto.getGoalType() + " at " + dto.getLevel() + " level with " + dto.getEquipment();

        return Workout.builder()
                .goalType(dto.getGoalType())
                .level(dto.getLevel())
                .equipment(dto.getEquipment())
                .daysPerWeek(dto.getDaysPerWeek())
                .quietTraining(dto.isQuietTraining())
                .limitedSpace(dto.isLimitedSpace())
                .excludedExercises(dto.getExcludedExercises())
                .name(name)
                .description(description)
                .build();
    }
}



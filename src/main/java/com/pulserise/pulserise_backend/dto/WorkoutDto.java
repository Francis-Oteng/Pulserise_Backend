package com.pulserise.pulserise_backend.dto;

import com.pulserise.pulserise_backend.enums.GoalType;
import com.pulserise.pulserise_backend.enums.WorkoutLevel;
import com.pulserise.pulserise_backend.enums.Equipment;
import lombok.Data;

@Data
public class WorkoutDto {
    private GoalType goalType;
    private WorkoutLevel level;
    private Equipment equipment;
    private int daysPerWeek;
    private boolean quietTraining;
    private boolean limitedSpace;
    private String excludedExercises;
}
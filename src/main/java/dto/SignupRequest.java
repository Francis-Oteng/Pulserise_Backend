package com.pulserise.workoutapp.dto;

import java.util.List;
import com.workout.app.model.UserProfile.ActivityLevel;
import com.workout.app.model.UserProfile.FitnessGoal;
import com.workout.app.model.UserProfile.Gender;
import com.workout.app.model.UserProfile.TargetArea;
import com.workout.app.model.UserProfile.WorkoutDuration;
import com.workout.app.model.UserProfile.WorkoutFrequency;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
public class SignupRequest {
    @Data
        @NotNull
        @Min(10)
        @Max(100)
        private Integer age;

        @NotNull
        private Gender gender;

        @NotNull
        @Min(100)
        @Max(250)
        private Integer height;

        @NotNull
        @Min(30)
        @Max(200)
        private Integer weight;

        @NotNull
        private FitnessGoal fitnessGoal;

        @NotNull
        private ActivityLevel activityLevel;

        @NotNull
        private WorkoutFrequency workoutFrequency;

        @NotNull
        private WorkoutDuration workoutDuration;

        @NotNull
        private List<TargetArea> targetAreas;

        private String physicalLimitations;

        private List<String> availableEquipment;
    }



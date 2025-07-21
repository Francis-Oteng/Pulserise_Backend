package com.pulserise.pulserise.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExerciseRequestDTO {
    private String name;
    private List<String> muscleGroups;
    private List<String> equipment;
    private String difficultyLevel;
    private String instructions;
    private int duration; // minutes per set
    private int caloriesBurned;
}

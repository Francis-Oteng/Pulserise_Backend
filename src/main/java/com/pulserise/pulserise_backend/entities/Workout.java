package com.pulserise.pulserise_backend.entities;

import com.pulserise.pulserise_backend.enums.GoalType;
import com.pulserise.pulserise_backend.enums.WorkoutLevel;
import com.pulserise.pulserise_backend.enums.Equipment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @Enumerated(EnumType.STRING)
    private WorkoutLevel level;

    @Enumerated(EnumType.STRING)
    private Equipment equipment;

    private int daysPerWeek;
    private boolean quietTraining;
    private boolean limitedSpace;
    private String excludedExercises;
    private String name;
    private String description;
}
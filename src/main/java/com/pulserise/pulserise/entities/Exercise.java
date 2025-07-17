package com.pulserise.pulserise.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection
    @CollectionTable(name = "exercise_muscle_groups", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle_group")
    private List<String> muscleGroups;

    @ElementCollection
    @CollectionTable(name = "exercise_equipment", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "equipment")
    private List<String> equipment;

    @Column(name = "difficulty_level")
    private String difficultyLevel;

    private String instructions;
    private int duration; // minutes per set
    private int caloriesBurned;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getMuscleGroups() { return muscleGroups; }
    public void setMuscleGroups(List<String> muscleGroups) { this.muscleGroups = muscleGroups; }

    public List<String> getEquipment() { return equipment; }
    public void setEquipment(List<String> equipment) { this.equipment = equipment; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
} 
package com.pulserise.pulserise_backend.Controller;

import com.pulserise.pulserise_backend.Service.WorkoutService;
import com.pulserise.pulserise_backend.dto.WorkoutDto;
import com.pulserise.pulserise_backend.dto.ApiResponse;
import com.pulserise.pulserise_backend.entities.Workout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout")
public class WorkOutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkOutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    /**
     * Generate a custom workout plan based on user preferences.
     * @param dto User's workout preferences and constraints
     * @return Generated Workout plan
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Workout>> generateWorkout(@RequestBody WorkoutDto dto) {
        Workout workout = workoutService.generateWorkout(dto);
        ApiResponse<Workout> response = new ApiResponse<>(true, "Workout generated successfully", workout);
        return ResponseEntity.ok(response);
    }
}
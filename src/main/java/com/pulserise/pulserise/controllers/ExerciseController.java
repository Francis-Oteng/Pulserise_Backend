package com.pulserise.pulserise.controllers;

import com.pulserise.pulserise.dto.ApiResponse;
import com.pulserise.pulserise.dto.ExerciseRequestDTO;
import com.pulserise.pulserise.entities.Exercise;
import com.pulserise.pulserise.service.ExerciseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody ExerciseRequestDTO exerciseRequestDTO, HttpServletRequest request) {
        try {
            Exercise newExercise = exerciseService.createExercise(exerciseRequestDTO, request);
            return new ResponseEntity<>(newExercise, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllExercises() {
        try {
            ApiResponse apiResponse = exerciseService.getAllExercises();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse apiResponse = exerciseService.getAllExercises();
            apiResponse.setMessage(e.getMessage());
            apiResponse.setSuccess(false);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getExerciseById(@PathVariable Long id) {
        try {
            ApiResponse apiResponse = exerciseService.getExerciseById(id);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse apiResponse = exerciseService.getExerciseById(id);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setSuccess(false);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteExerciseById(@RequestBody Long id) {
        try {
            exerciseService.deleteExerciseById(id);
            return new ResponseEntity<>("Exercise deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

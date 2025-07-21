package com.pulserise.pulserise.service;

import com.pulserise.pulserise.Exception.ResourceNotFoundException;
import com.pulserise.pulserise.Exception.UnauthorizedException;
import com.pulserise.pulserise.dto.ApiResponse;
import com.pulserise.pulserise.dto.ExerciseRequestDTO;
import com.pulserise.pulserise.entities.Exercise;
import com.pulserise.pulserise.entities.User;
import com.pulserise.pulserise.repository.ExerciseRepository;
import com.pulserise.pulserise.repository.UserRepository;
import com.pulserise.pulserise.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    public ExerciseService(ExerciseRepository exerciseRepository, UserRepository userRepository, AuthUtil authUtil) {
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }

    public Exercise createExercise(ExerciseRequestDTO exerciseRequestDTO, HttpServletRequest request) {

        User user = userRepository.findById(authUtil.getUserIdFromToken(request)).orElseThrow(
                () -> new UnauthorizedException("You are not logged in!")
        );

        Exercise newExercise = new Exercise();
        newExercise.setName(exerciseRequestDTO.getName());
        newExercise.setMuscleGroups(exerciseRequestDTO.getMuscleGroups());
        newExercise.setDuration(exerciseRequestDTO.getDuration());
        newExercise.setCaloriesBurned(exerciseRequestDTO.getCaloriesBurned());
        newExercise.setMuscleGroups(exerciseRequestDTO.getMuscleGroups());
        newExercise.setDifficultyLevel(exerciseRequestDTO.getDifficultyLevel());
        newExercise.setInstructions(exerciseRequestDTO.getInstructions());

        return exerciseRepository.save(newExercise);
    }

    public ApiResponse getAllExercises() {
        try {

            List<Exercise> exercises = exerciseRepository.findAll();
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage("Excercises retrieved successfully");
            response.setData(exercises);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving exercises", e);
        }
    }

    public ApiResponse getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setMessage("Exercise retrieved successfully");
        response.setData(exercise);
        return response;
    }

    public void deleteExerciseById(Long id) {
        exerciseRepository.deleteById(id);
    }
}

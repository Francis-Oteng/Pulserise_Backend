package com.pulserise.pulserise.controllers;

import com.pulserise.pulserise.dto.ApiResponse;
import com.pulserise.pulserise.dto.GoalRequestDTO;
import com.pulserise.pulserise.entities.Goal;
import com.pulserise.pulserise.service.GoalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody GoalRequestDTO goalRequestDTO, HttpServletRequest request) {
        Goal newGoal = goalService.createGoal(goalRequestDTO, request);
        return new ResponseEntity<>(newGoal, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllGoals() {
        try {
            ApiResponse apiResponse = goalService.getAllGoals();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setData(null);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getGoalById(@PathVariable Long id) {
        try {
            ApiResponse apiResponse = goalService.getGoalById(id);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setData(null);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

package com.pulserise.pulserise.service;

import com.pulserise.pulserise.Exception.ResourceNotFoundException;
import com.pulserise.pulserise.Exception.UnauthorizedException;
import com.pulserise.pulserise.dto.ApiResponse;
import com.pulserise.pulserise.dto.GoalRequestDTO;
import com.pulserise.pulserise.entities.User;
import com.pulserise.pulserise.entities.Goal;
import com.pulserise.pulserise.repository.GoalRepository;
import com.pulserise.pulserise.repository.UserRepository;
import com.pulserise.pulserise.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, AuthUtil authUtil, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.authUtil = authUtil;
        this.userRepository = userRepository;
    }

    public Goal createGoal(GoalRequestDTO goalRequestDTO, HttpServletRequest request) {
        User user = userRepository.findById(authUtil.getUserIdFromToken(request)).orElseThrow(
                () -> new UnauthorizedException("You are not logged in!")
        );

        Goal goal = new Goal();
        goal.setName(goalRequestDTO.getName());
        goal.setDescription(goalRequestDTO.getDescription());
        goal.setUser(user);
        return goalRepository.save(goal);
    }

    public ApiResponse getAllGoals() {
        List<Goal> goals = goalRepository.findAll();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("All goals found");
        apiResponse.setData(goals);
        return apiResponse;
    }

    public ApiResponse getGoalById(Long id) {
        Goal goal = goalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Goal not found")
        );
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Goal found");
        apiResponse.setData(goal);
        return apiResponse;
    }

    public ApiResponse deleteGoalById(Long id) {
        Goal goal = goalRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Goal not found")
        );
        goalRepository.delete(goal);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(true);
        apiResponse.setMessage("Goal deleted");
        apiResponse.setData(goal);
        return apiResponse;
    }
}

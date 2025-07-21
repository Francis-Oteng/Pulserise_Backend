package com.pulserise.workoutapp.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workout.app.dto.UserProfileDto;
import com.workout.app.exception.CustomException;
import com.workout.app.model.User;
import com.workout.app.model.UserProfile;
import com.workout.app.repository.UserProfileRepository;
import com.workout.app.repository.UserRepository;
import com.workout.app.security.UserDetailsImpl;

import java.util.Optional;

@Service
public class UserProfileService {
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public UserProfile createOrUpdateUserProfile(UserDetailsImpl userDetails, UserProfileDto profileDto) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException("User not found"));

        Optional<UserProfile> existingProfile = userProfileRepository.findByUser(user);

        UserProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
        } else {
            profile = new UserProfile();
            profile.setUser(user);
        }

        // Update profile fields
        profile.setAge(profileDto.getAge());
        profile.setGender(profileDto.getGender());
        profile.setHeight(profileDto.getHeight());
        profile.setWeight(profileDto.getWeight());
        profile.setFitnessGoal(profileDto.getFitnessGoal());
        profile.setActivityLevel(profileDto.getActivityLevel());
        profile.setWorkoutFrequency(profileDto.getWorkoutFrequency());
        profile.setWorkoutDuration(profileDto.getWorkoutDuration());
        profile.setTargetAreas(profileDto.getTargetAreas());
        profile.setPhysicalLimitations(profileDto.getPhysicalLimitations());
        profile.setAvailableEquipment(profileDto.getAvailableEquipment());

        UserProfile savedProfile = userProfileRepository.save(profile);

        // Mark user as having completed profile
        if (!user.isProfileCompleted()) {
            user.setProfileCompleted(true);
            userRepository.save(user);
        }

        return savedProfile;
    }

    public UserProfile getUserProfile(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException("User not found"));

        return userProfileRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Profile not found"));
    }
}
package com.pulserise.workoutapp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.workout.app.dto.UserProfileDto;
import com.workout.app.model.UserProfile;
import com.workout.app.security.UserDetailsImpl;
import com.workout.app.service.UserProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfile> createOrUpdateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserProfileDto profileDto) {
        UserProfile profile = userProfileService.createOrUpdateUserProfile(userDetails, profileDto);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<UserProfile> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserProfile profile = userProfileService.getUserProfile(userDetails);
        return ResponseEntity.ok(profile);
    }
}
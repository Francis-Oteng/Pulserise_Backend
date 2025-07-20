package com.pulserise.pulserise_backend.Service;

import com.pulserise.pulserise_backend.entities.Progress;
import com.pulserise.pulserise_backend.Repository.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    @Autowired
    public ProgressService(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    // Track user progress (stub)
    public String trackProgress(String username) {
        // Implement logic to track and return user progress
        return "Progress for user: " + username;
    }

    // Save progress
    public Progress saveProgress(Progress progress) {
        return progressRepository.save(progress);
    }

    // Update progress
    public Progress updateProgress(Long id, Progress updatedProgress) {
        Optional<Progress> optionalProgress = progressRepository.findById(id);
        if (optionalProgress.isPresent()) {
            Progress progress = optionalProgress.get();
            progress.setDetails(updatedProgress.getDetails());
            progress.setDate(updatedProgress.getDate());
            // Add more fields as needed
            return progressRepository.save(progress);
        }
        return null;
    }

    // Retrieve progress by user
    public List<Progress> getProgressByUser(String username) {
        return progressRepository.findByUsername(username);
    }

    // Retrieve progress by id
    public Optional<Progress> getProgressById(Long id) {
        return progressRepository.findById(id);
    }

    // Delete progress
    public void deleteProgress(Long id) {
        progressRepository.deleteById(id);
    }
}
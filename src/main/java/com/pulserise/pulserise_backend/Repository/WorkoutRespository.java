package com.pulserise.pulserise_backend.Repository;

import com.pulserise.pulserise_backend.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutRespository extends JpaRepository<Workout, Long> {
    // Add custom queries if needed
}

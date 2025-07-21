package com.pulserise.pulserise.repository;

import com.pulserise.pulserise.entities.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}

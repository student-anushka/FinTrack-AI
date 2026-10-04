package com.fintrack.goal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintrack.goal.entity.GoalContribution;

public interface GoalContributionRepository
        extends JpaRepository<GoalContribution, Long> {
}
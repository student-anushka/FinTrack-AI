package com.fintrack.goal.repository;

import java.util.List;
import java.util.Optional;
import com.fintrack.goal.entity.FinancialGoal;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FinancialGoalRepository
        extends JpaRepository<FinancialGoal, Long> {

    List<FinancialGoal> findByUserId(Long userId);

    Optional<FinancialGoal> findByIdAndUserId(
            Long id,
            Long userId);
}
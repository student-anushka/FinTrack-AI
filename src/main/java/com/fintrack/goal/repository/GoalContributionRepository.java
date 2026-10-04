package com.fintrack.goal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.fintrack.goal.entity.GoalContribution;
import java.util.List;

public interface GoalContributionRepository
        extends JpaRepository<GoalContribution, Long>,
        JpaSpecificationExecutor<GoalContribution> {

    Page<GoalContribution> findByGoalId(
            Long goalId,
            Pageable pageable);

    List<GoalContribution> findTop10ByGoalUserIdOrderByContributionDateDesc(
            Long userId);
}
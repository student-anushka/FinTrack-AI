package com.fintrack.budget.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.fintrack.budget.entity.Budget;
import java.util.List;

public interface BudgetRepository
                extends JpaRepository<Budget, Long>,
                JpaSpecificationExecutor<Budget> {

        Page<Budget> findByUserId(Long userId, Pageable pageable);

        Optional<Budget> findByIdAndUserId(Long id, Long userId);

        List<Budget> findAllByUserId(Long userId);

        @Query("""
                        SELECT COUNT(b) > 0
                        FROM Budget b
                        WHERE b.user.id = :userId
                        AND b.category.id = :categoryId
                        AND b.startDate <= :endDate
                        AND b.endDate >= :startDate
                        AND (:budgetId IS NULL OR b.id <> :budgetId)
                        """)
        boolean existsOverlappingBudget(
                        @Param("userId") Long userId,
                        @Param("categoryId") Long categoryId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("budgetId") Long budgetId);
}
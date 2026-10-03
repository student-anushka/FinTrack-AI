package com.fintrack.budget.repository;

import com.fintrack.budget.entity.Budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository
                extends JpaRepository<Budget, Long> {

        List<Budget> findByUserId(Long userId);

        Optional<Budget> findByIdAndUserId(
                        Long id,
                        Long userId);

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
                        @Param("startDate") java.time.LocalDate startDate,
                        @Param("endDate") java.time.LocalDate endDate,
                        @Param("budgetId") Long budgetId);
}
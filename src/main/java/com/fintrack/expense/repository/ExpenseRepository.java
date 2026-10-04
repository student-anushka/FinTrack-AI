package com.fintrack.expense.repository;

import com.fintrack.expense.entity.Expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ExpenseRepository
                extends JpaRepository<Expense, Long>,
                JpaSpecificationExecutor<Expense> {

        Optional<Expense> findByIdAndUserId(
                        Long id,
                        Long userId);

        @Query("""
                        SELECT COALESCE(SUM(e.amount), 0)
                        FROM Expense e
                        WHERE e.user.id = :userId
                        AND e.category.id = :categoryId
                        AND e.expenseDate BETWEEN :startDate AND :endDate
                        """)
        BigDecimal calculateTotalSpent(
                        @Param("userId") Long userId,
                        @Param("categoryId") Long categoryId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("""
                        SELECT COALESCE(SUM(e.amount), 0)
                        FROM Expense e
                        WHERE e.user.id = :userId
                        """)
        BigDecimal calculateTotalExpense(
                        @Param("userId") Long userId);

        @Query("""
                        SELECT COALESCE(SUM(e.amount), 0)
                        FROM Expense e
                        WHERE e.user.id = :userId
                        AND e.expenseDate BETWEEN :startDate AND :endDate
                        """)
        BigDecimal calculateExpenseBetweenDates(
                        @Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}
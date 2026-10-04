package com.fintrack.dashboard.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.fintrack.expense.entity.Expense;

public interface DashboardRepository
        extends Repository<Expense, Long> {

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
            """)
    BigDecimal getTotalExpense(
            @Param("userId") Long userId);

    @Query("""
            SELECT COALESCE(SUM(i.amount), 0)
            FROM Income i
            WHERE i.user.id = :userId
            """)
    BigDecimal getTotalIncome(
            @Param("userId") Long userId);

    @Query("""
            SELECT COALESCE(SUM(b.amount), 0)
            FROM Budget b
            WHERE b.user.id = :userId
            """)
    BigDecimal getTotalBudget(
            @Param("userId") Long userId);

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
            """)
    BigDecimal getTotalBudgetSpent(
            @Param("userId") Long userId);
}
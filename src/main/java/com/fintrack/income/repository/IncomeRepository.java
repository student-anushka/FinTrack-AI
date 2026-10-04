package com.fintrack.income.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fintrack.income.entity.Income;

public interface IncomeRepository
                extends JpaRepository<Income, Long>,
                JpaSpecificationExecutor<Income> {

        Page<Income> findByUserId(Long userId, Pageable pageable);

        Optional<Income> findByIdAndUserId(
                        Long id,
                        Long userId);

        @Query("""
        SELECT COALESCE(SUM(i.amount), 0)
        FROM Income i
        WHERE i.user.id = :userId
        """)
BigDecimal calculateTotalIncome(
        @Param("userId") Long userId);

@Query("""
        SELECT COALESCE(SUM(i.amount), 0)
        FROM Income i
        WHERE i.user.id = :userId
        AND i.incomeDate BETWEEN :startDate AND :endDate
        """)
BigDecimal calculateIncomeBetweenDates(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);                
}
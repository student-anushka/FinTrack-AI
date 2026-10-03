package com.fintrack.income.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintrack.income.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    Optional<Income> findByIdAndUserId(Long id, Long userId);
}
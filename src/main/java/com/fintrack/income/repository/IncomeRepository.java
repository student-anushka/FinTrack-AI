package com.fintrack.income.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fintrack.income.entity.Income;

public interface IncomeRepository
                extends JpaRepository<Income, Long>,
                JpaSpecificationExecutor<Income> {

        Page<Income> findByUserId(Long userId, Pageable pageable);

        Optional<Income> findByIdAndUserId(
                        Long id,
                        Long userId);
}
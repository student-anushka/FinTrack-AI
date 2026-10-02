package com.fintrack.expense.repository;

import com.fintrack.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);

    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);
}
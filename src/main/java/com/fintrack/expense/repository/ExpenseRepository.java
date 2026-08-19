package com.fintrack.expense.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fintrack.expense.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);

    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);
}
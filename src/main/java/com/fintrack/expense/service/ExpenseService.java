package com.fintrack.expense.service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.common.exception.InvalidExpenseException;
import com.fintrack.expense.dto.CreateExpenseRequest;
import com.fintrack.expense.dto.ExpenseResponse;
import com.fintrack.expense.entity.Category;
import com.fintrack.expense.entity.Expense;
import com.fintrack.expense.repository.CategoryRepository;
import com.fintrack.expense.repository.ExpenseRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {

        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public ExpenseResponse createExpense(
            CreateExpenseRequest request,
            Authentication authentication) {

        // 1. Get authenticated user's email from JWT
        String email = authentication.getName();

        // 2. Find the user in database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Validate expense date
        if (request.getExpenseDate().isAfter(LocalDate.now())) {
            throw new InvalidExpenseException(
        "Expense date cannot be in the future");
        }

        // 4. Find category
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 5. Create Expense entity
        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setPaymentMethod(request.getPaymentMethod());

        // 6. Connect expense with authenticated user
        expense.setUser(user);

        // 7. Connect expense with category
        expense.setCategory(category);

        // 8. Set audit timestamps
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());

        // 9. Save to database
        Expense savedExpense = expenseRepository.save(expense);

        // 10. Convert Entity to Response DTO
        return new ExpenseResponse(
                savedExpense.getId(),
                savedExpense.getTitle(),
                savedExpense.getAmount(),
                savedExpense.getDescription(),
                savedExpense.getExpenseDate(),
                savedExpense.getPaymentMethod(),
                savedExpense.getCategory().getId(),
                savedExpense.getCategory().getName());
    }
}
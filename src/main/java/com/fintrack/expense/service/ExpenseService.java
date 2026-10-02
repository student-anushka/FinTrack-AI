package com.fintrack.expense.service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.common.exception.CategoryNotFoundException;
import com.fintrack.common.exception.ExpenseNotFoundException;
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
import java.util.List;

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

    // =========================
    // CREATE EXPENSE
    // =========================

    public ExpenseResponse createExpense(
            CreateExpenseRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        validateExpenseDate(request.getExpenseDate());

        Category category = getCategory(request.getCategoryId());

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setUser(user);
        expense.setCategory(category);

        LocalDateTime now = LocalDateTime.now();

        expense.setCreatedAt(now);
        expense.setUpdatedAt(now);

        Expense savedExpense = expenseRepository.save(expense);

        return mapToResponse(savedExpense);
    }

    // =========================
    // GET MY EXPENSES
    // =========================

    public List<ExpenseResponse> getMyExpenses(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        List<Expense> expenses =
                expenseRepository.findByUserIdOrderByExpenseDateDesc(
                        user.getId());

        return expenses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // GET EXPENSE BY ID
    // =========================

    public ExpenseResponse getExpenseById(
            Long expenseId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Expense expense = expenseRepository
                .findByIdAndUserId(expenseId, user.getId())
                .orElseThrow(() ->
                        new ExpenseNotFoundException(
                                "Expense not found"));

        return mapToResponse(expense);
    }

    // =========================
    // UPDATE EXPENSE
    // =========================

    public ExpenseResponse updateExpense(
            Long expenseId,
            CreateExpenseRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Expense expense = expenseRepository
                .findByIdAndUserId(expenseId, user.getId())
                .orElseThrow(() ->
                        new ExpenseNotFoundException(
                                "Expense not found"));

        validateExpenseDate(request.getExpenseDate());

        Category category = getCategory(request.getCategoryId());

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setCategory(category);

        expense.setUpdatedAt(LocalDateTime.now());

        Expense updatedExpense = expenseRepository.save(expense);

        return mapToResponse(updatedExpense);
    }

    // =========================
    // DELETE EXPENSE
    // =========================

    public void deleteExpense(
            Long expenseId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Expense expense = expenseRepository
                .findByIdAndUserId(expenseId, user.getId())
                .orElseThrow(() ->
                        new ExpenseNotFoundException(
                                "Expense not found"));

        expenseRepository.delete(expense);
    }

    // =========================
    // HELPER METHODS
    // =========================

    private User getAuthenticatedUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private Category getCategory(Long categoryId) {

        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found"));
    }

    private void validateExpenseDate(LocalDate expenseDate) {

        if (expenseDate.isAfter(LocalDate.now())) {

            throw new InvalidExpenseException(
                    "Expense date cannot be in the future");
        }
    }

    private ExpenseResponse mapToResponse(Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getPaymentMethod(),
                expense.getCategory().getId(),
                expense.getCategory().getName()
        );
    }
}
package com.fintrack.expense.service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.common.exception.CategoryNotFoundException;
import com.fintrack.common.exception.ExpenseNotFoundException;
import com.fintrack.common.exception.InvalidExpenseException;
import com.fintrack.expense.dto.CreateExpenseRequest;
import com.fintrack.expense.dto.ExpensePageResponse;
import com.fintrack.expense.dto.ExpenseResponse;
import com.fintrack.expense.entity.Category;
import com.fintrack.expense.entity.Expense;
import com.fintrack.expense.repository.CategoryRepository;
import com.fintrack.expense.repository.ExpenseRepository;
import com.fintrack.expense.specification.ExpenseSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        // Pagination + Sorting +
        // Filtering + Search
        // =========================

        public ExpensePageResponse filterExpenses(
                        Authentication authentication,
                        Long categoryId,
                        LocalDate startDate,
                        LocalDate endDate,
                        BigDecimal minAmount,
                        BigDecimal maxAmount,
                        String search,
                        int page,
                        int size,
                        String sortBy,
                        String direction) {

                User user = getAuthenticatedUser(authentication);

                validatePagination(page, size);

                validateFilters(
                                startDate,
                                endDate,
                                minAmount,
                                maxAmount);

                Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC;

                String safeSortBy = validateSortField(sortBy);

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(
                                                sortDirection,
                                                safeSortBy));

                Specification<Expense> specification = ExpenseSpecification.filterExpenses(
                                user.getId(),
                                categoryId,
                                startDate,
                                endDate,
                                minAmount,
                                maxAmount,
                                search);

                Page<Expense> expensePage = expenseRepository.findAll(
                                specification,
                                pageable);

                List<ExpenseResponse> expenses = expensePage.getContent()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();

                return new ExpensePageResponse(
                                expenses,
                                expensePage.getNumber(),
                                expensePage.getTotalPages(),
                                expensePage.getTotalElements(),
                                expensePage.getSize());
        }

        // =========================
        // GET EXPENSE BY ID
        // =========================

        public ExpenseResponse getExpenseById(
                        Long expenseId,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                Expense expense = expenseRepository
                                .findByIdAndUserId(
                                                expenseId,
                                                user.getId())
                                .orElseThrow(() -> new ExpenseNotFoundException(
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
                                .findByIdAndUserId(
                                                expenseId,
                                                user.getId())
                                .orElseThrow(() -> new ExpenseNotFoundException(
                                                "Expense not found"));

                validateExpenseDate(
                                request.getExpenseDate());

                Category category = getCategory(request.getCategoryId());

                expense.setTitle(request.getTitle());
                expense.setAmount(request.getAmount());
                expense.setDescription(request.getDescription());
                expense.setExpenseDate(request.getExpenseDate());
                expense.setPaymentMethod(request.getPaymentMethod());
                expense.setCategory(category);

                expense.setUpdatedAt(
                                LocalDateTime.now());

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
                                .findByIdAndUserId(
                                                expenseId,
                                                user.getId())
                                .orElseThrow(() -> new ExpenseNotFoundException(
                                                "Expense not found"));

                expenseRepository.delete(expense);
        }

        // =========================
        // GET AUTHENTICATED USER
        // =========================

        private User getAuthenticatedUser(
                        Authentication authentication) {

                String email = authentication.getName();

                return userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found"));
        }

        // =========================
        // GET CATEGORY
        // =========================

        private Category getCategory(
                        Long categoryId) {

                return categoryRepository
                                .findById(categoryId)
                                .orElseThrow(() -> new CategoryNotFoundException(
                                                "Category not found"));
        }

        // =========================
        // EXPENSE DATE VALIDATION
        // =========================

        private void validateExpenseDate(
                        LocalDate expenseDate) {

                if (expenseDate.isAfter(
                                LocalDate.now())) {

                        throw new InvalidExpenseException(
                                        "Expense date cannot be in the future");
                }
        }

        // =========================
        // PAGINATION VALIDATION
        // =========================

        private void validatePagination(
                        int page,
                        int size) {

                if (page < 0) {

                        throw new InvalidExpenseException(
                                        "Page number cannot be negative");
                }

                if (size < 1 || size > 100) {

                        throw new InvalidExpenseException(
                                        "Page size must be between 1 and 100");
                }
        }

        // =========================
        // FILTER VALIDATION
        // =========================

        private void validateFilters(
                        LocalDate startDate,
                        LocalDate endDate,
                        BigDecimal minAmount,
                        BigDecimal maxAmount) {

                if (startDate != null
                                && endDate != null
                                && startDate.isAfter(endDate)) {

                        throw new InvalidExpenseException(
                                        "Start date cannot be after end date");
                }

                if (minAmount != null
                                && minAmount.compareTo(
                                                BigDecimal.ZERO) < 0) {

                        throw new InvalidExpenseException(
                                        "Minimum amount cannot be negative");
                }

                if (maxAmount != null
                                && maxAmount.compareTo(
                                                BigDecimal.ZERO) < 0) {

                        throw new InvalidExpenseException(
                                        "Maximum amount cannot be negative");
                }

                if (minAmount != null
                                && maxAmount != null
                                && minAmount.compareTo(
                                                maxAmount) > 0) {

                        throw new InvalidExpenseException(
                                        "Minimum amount cannot be greater than maximum amount");
                }
        }

        // =========================
        // SAFE SORTING
        // =========================

        private String validateSortField(
                        String sortBy) {

                return switch (sortBy) {

                        case "id",
                                        "title",
                                        "amount",
                                        "expenseDate",
                                        "createdAt",
                                        "updatedAt" ->
                                sortBy;

                        default -> "expenseDate";
                };
        }

        // =========================
        // ENTITY -> DTO
        // =========================

        private ExpenseResponse mapToResponse(
                        Expense expense) {

                return new ExpenseResponse(
                                expense.getId(),
                                expense.getTitle(),
                                expense.getAmount(),
                                expense.getDescription(),
                                expense.getExpenseDate(),
                                expense.getPaymentMethod(),
                                expense.getCategory().getId(),
                                expense.getCategory().getName());
        }
}
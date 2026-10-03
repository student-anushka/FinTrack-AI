package com.fintrack.budget.service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;

import com.fintrack.budget.dto.BudgetPageResponse;
import com.fintrack.budget.dto.BudgetResponse;
import com.fintrack.budget.dto.CreateBudgetRequest;
import com.fintrack.budget.entity.Budget;
import com.fintrack.budget.repository.BudgetRepository;
import com.fintrack.budget.specification.BudgetSpecification;

import com.fintrack.common.exception.BudgetNotFoundException;
import com.fintrack.common.exception.CategoryNotFoundException;
import com.fintrack.common.exception.InvalidBudgetException;

import com.fintrack.expense.entity.Category;
import com.fintrack.expense.repository.CategoryRepository;
import com.fintrack.expense.repository.ExpenseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetService {

        private final BudgetRepository budgetRepository;
        private final CategoryRepository categoryRepository;
        private final UserRepository userRepository;
        private final ExpenseRepository expenseRepository;

        public BudgetService(
                        BudgetRepository budgetRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        ExpenseRepository expenseRepository) {

                this.budgetRepository = budgetRepository;
                this.categoryRepository = categoryRepository;
                this.userRepository = userRepository;
                this.expenseRepository = expenseRepository;
        }

        // =========================
        // CREATE
        // =========================

        public BudgetResponse createBudget(
                        CreateBudgetRequest request,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                validateBudgetDates(
                                request.getStartDate(),
                                request.getEndDate());

                Category category = getCategory(request.getCategoryId());

                validateNoOverlappingBudget(
                                user.getId(),
                                category.getId(),
                                request.getStartDate(),
                                request.getEndDate(),
                                null);

                Budget budget = new Budget();

                budget.setName(request.getName());
                budget.setAmount(request.getAmount());
                budget.setStartDate(request.getStartDate());
                budget.setEndDate(request.getEndDate());
                budget.setUser(user);
                budget.setCategory(category);

                LocalDateTime now = LocalDateTime.now();

                budget.setCreatedAt(now);
                budget.setUpdatedAt(now);

                Budget savedBudget = budgetRepository.save(budget);

                return mapToResponse(savedBudget);
        }

        // =========================
        // GET / SEARCH / FILTER
        // =========================

        public BudgetPageResponse filterBudgets(
                        Authentication authentication,
                        Long categoryId,
                        LocalDate startDate,
                        LocalDate endDate,
                        String search,
                        int page,
                        int size,
                        String sortBy,
                        String direction) {

                User user = getAuthenticatedUser(authentication);

                validatePagination(page, size);

                validateFilters(
                                startDate,
                                endDate);

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

                Specification<Budget> specification = BudgetSpecification.filterBudgets(
                                user.getId(),
                                categoryId,
                                startDate,
                                endDate,
                                search);

                Page<Budget> budgetPage = budgetRepository.findAll(
                                specification,
                                pageable);

                List<BudgetResponse> budgets = budgetPage.getContent()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();

                return new BudgetPageResponse(
                                budgets,
                                budgetPage.getNumber(),
                                budgetPage.getTotalPages(),
                                budgetPage.getTotalElements(),
                                budgetPage.getSize());
        }

        // =========================
        // GET BY ID
        // =========================

        public BudgetResponse getBudgetById(
                        Long budgetId,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                Budget budget = budgetRepository
                                .findByIdAndUserId(
                                                budgetId,
                                                user.getId())
                                .orElseThrow(() -> new BudgetNotFoundException(
                                                "Budget not found"));

                return mapToResponse(budget);
        }

        // =========================
        // UPDATE
        // =========================

        public BudgetResponse updateBudget(
                        Long budgetId,
                        CreateBudgetRequest request,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                Budget budget = budgetRepository
                                .findByIdAndUserId(
                                                budgetId,
                                                user.getId())
                                .orElseThrow(() -> new BudgetNotFoundException(
                                                "Budget not found"));

                validateBudgetDates(
                                request.getStartDate(),
                                request.getEndDate());

                Category category = getCategory(request.getCategoryId());

                validateNoOverlappingBudget(
                                user.getId(),
                                category.getId(),
                                request.getStartDate(),
                                request.getEndDate(),
                                budgetId);

                budget.setName(request.getName());
                budget.setAmount(request.getAmount());
                budget.setStartDate(request.getStartDate());
                budget.setEndDate(request.getEndDate());
                budget.setCategory(category);
                budget.setUpdatedAt(
                                LocalDateTime.now());

                Budget updatedBudget = budgetRepository.save(budget);

                return mapToResponse(updatedBudget);
        }

        // =========================
        // DELETE
        // =========================

        public void deleteBudget(
                        Long budgetId,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                Budget budget = budgetRepository
                                .findByIdAndUserId(
                                                budgetId,
                                                user.getId())
                                .orElseThrow(() -> new BudgetNotFoundException(
                                                "Budget not found"));

                budgetRepository.delete(budget);
        }

        // =========================
        // AUTHENTICATED USER
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
        // CATEGORY
        // =========================

        private Category getCategory(
                        Long categoryId) {

                return categoryRepository
                                .findById(categoryId)
                                .orElseThrow(() -> new CategoryNotFoundException(
                                                "Category not found"));
        }

        // =========================
        // DATE VALIDATION
        // =========================

        private void validateBudgetDates(
                        LocalDate startDate,
                        LocalDate endDate) {

                if (startDate.isAfter(endDate)) {

                        throw new InvalidBudgetException(
                                        "Start date cannot be after end date");
                }
        }

        // =========================
        // FILTER VALIDATION
        // =========================

        private void validateFilters(
                        LocalDate startDate,
                        LocalDate endDate) {

                if (startDate != null
                                && endDate != null
                                && startDate.isAfter(endDate)) {

                        throw new InvalidBudgetException(
                                        "Filter start date cannot be after end date");
                }
        }

        // =========================
        // PAGINATION VALIDATION
        // =========================

        private void validatePagination(
                        int page,
                        int size) {

                if (page < 0) {

                        throw new InvalidBudgetException(
                                        "Page number cannot be negative");
                }

                if (size < 1 || size > 100) {

                        throw new InvalidBudgetException(
                                        "Page size must be between 1 and 100");
                }
        }

        // =========================
        // SAFE SORTING
        // =========================

        private String validateSortField(
                        String sortBy) {

                return switch (sortBy) {

                        case "id",
                                        "name",
                                        "amount",
                                        "startDate",
                                        "endDate",
                                        "createdAt",
                                        "updatedAt" ->
                                sortBy;

                        default -> "startDate";
                };
        }

        // =========================
        // OVERLAPPING BUDGET
        // =========================

        private void validateNoOverlappingBudget(
                        Long userId,
                        Long categoryId,
                        LocalDate startDate,
                        LocalDate endDate,
                        Long budgetId) {

                boolean exists = budgetRepository
                                .existsOverlappingBudget(
                                                userId,
                                                categoryId,
                                                startDate,
                                                endDate,
                                                budgetId);

                if (exists) {

                        throw new InvalidBudgetException(
                                        "An overlapping budget already exists for this category");
                }
        }

        // =========================
        // RESPONSE
        // =========================

        private BudgetResponse mapToResponse(
                        Budget budget) {

                BigDecimal spentAmount = expenseRepository.calculateTotalSpent(
                                budget.getUser().getId(),
                                budget.getCategory().getId(),
                                budget.getStartDate(),
                                budget.getEndDate());

                if (spentAmount == null) {
                        spentAmount = BigDecimal.ZERO;
                }

                BigDecimal remainingAmount = budget.getAmount()
                                .subtract(spentAmount);

                BigDecimal percentageUsed = spentAmount
                                .multiply(
                                                BigDecimal.valueOf(100))
                                .divide(
                                                budget.getAmount(),
                                                2,
                                                RoundingMode.HALF_UP);

                String status = calculateStatus(
                                budget,
                                spentAmount);

                return new BudgetResponse(
                                budget.getId(),
                                budget.getName(),
                                budget.getAmount(),
                                budget.getStartDate(),
                                budget.getEndDate(),
                                budget.getCategory().getId(),
                                budget.getCategory().getName(),
                                spentAmount,
                                remainingAmount,
                                percentageUsed,
                                status);
        }

        // =========================
        // STATUS
        // =========================

        private String calculateStatus(
                        Budget budget,
                        BigDecimal spentAmount) {

                LocalDate today = LocalDate.now();

                if (today.isBefore(
                                budget.getStartDate())) {

                        return "UPCOMING";
                }

                if (today.isAfter(
                                budget.getEndDate())) {

                        return "COMPLETED";
                }

                if (spentAmount.compareTo(
                                budget.getAmount()) >= 0) {

                        return "EXCEEDED";
                }

                return "ON_TRACK";
        }
}
package com.fintrack.dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.dashboard.dto.DashboardSummaryResponse;
import com.fintrack.dashboard.repository.DashboardRepository;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final UserRepository userRepository;

    public DashboardService(
            DashboardRepository dashboardRepository,
            UserRepository userRepository) {

        this.dashboardRepository = dashboardRepository;

        this.userRepository = userRepository;
    }

    public DashboardSummaryResponse getSummary(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Long userId = user.getId();

        BigDecimal totalIncome = dashboardRepository
                .getTotalIncome(userId);

        BigDecimal totalExpense = dashboardRepository
                .getTotalExpense(userId);

        BigDecimal totalBudget = dashboardRepository
                .getTotalBudget(userId);

        BigDecimal totalBudgetSpent = dashboardRepository
                .getTotalBudgetSpent(userId);

        BigDecimal totalSavings = totalIncome.subtract(totalExpense);

        BigDecimal savingsRate = calculatePercentage(
                totalSavings,
                totalIncome);

        BigDecimal budgetUsagePercentage = calculatePercentage(
                totalBudgetSpent,
                totalBudget);

        return new DashboardSummaryResponse(
                totalIncome,
                totalExpense,
                totalSavings,
                savingsRate,
                totalBudget,
                totalBudgetSpent,
                budgetUsagePercentage);
    }

    private BigDecimal calculatePercentage(
            BigDecimal value,
            BigDecimal total) {

        if (total == null ||
                total.compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        return value
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        total,
                        2,
                        RoundingMode.HALF_UP);
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Authenticated user not found"));
    }
}
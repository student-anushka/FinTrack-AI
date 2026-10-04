package com.fintrack.dashboard.controller;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.dashboard.dto.CategorySpendingResponse;
import com.fintrack.dashboard.dto.DashboardSummaryResponse;
import com.fintrack.dashboard.dto.MonthlyTrendResponse;
import com.fintrack.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.fintrack.budget.service.BudgetService;
import com.fintrack.dashboard.dto.BudgetUtilizationResponse;
import com.fintrack.budget.dto.BudgetResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final BudgetService budgetService;
    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    public DashboardController(
            DashboardService dashboardService,
            UserRepository userRepository,
            BudgetService budgetService) {

        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
        this.budgetService = budgetService;
    }

    private Long getLoggedInUserId(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        return user.getId();
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            Authentication authentication) {

        Long userId = getLoggedInUserId(authentication);

        return ResponseEntity.ok(
                dashboardService.getSummary(userId));
    }

    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendingResponse>> getCategoryWiseSpending(
            Authentication authentication) {

        Long userId = getLoggedInUserId(authentication);

        return ResponseEntity.ok(
                dashboardService.getCategoryWiseSpending(userId));
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthlyTrends(
            Authentication authentication) {

        Long userId = getLoggedInUserId(authentication);

        return ResponseEntity.ok(
                dashboardService.getMonthlyTrends(userId));
    }

    @GetMapping("/budget-utilization")
    public ResponseEntity<List<BudgetUtilizationResponse>> getBudgetUtilization(
            Authentication authentication) {

        Long userId = getLoggedInUserId(authentication);

        List<BudgetResponse> budgets = budgetService.getAllBudgetsForDashboard(userId);

        List<BudgetUtilizationResponse> response = budgets.stream()
                .map(budget -> new BudgetUtilizationResponse(
                        budget.getId(),
                        budget.getName(),
                        budget.getCategoryName(),
                        budget.getAmount(),
                        budget.getSpentAmount(),
                        budget.getRemainingAmount(),
                        budget.getPercentageUsed(),
                        budget.getStatus()))
                .toList();

        return ResponseEntity.ok(response);
    }
}
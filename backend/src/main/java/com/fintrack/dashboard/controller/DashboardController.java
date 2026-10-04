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
import com.fintrack.dashboard.dto.RecentActivityResponse;
import com.fintrack.dashboard.dto.FinancialHealthResponse;
import com.fintrack.dashboard.dto.GoalProgressResponse;
import com.fintrack.goal.dto.FinancialGoalResponse;
import com.fintrack.goal.service.FinancialGoalService;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final BudgetService budgetService;
    private final DashboardService dashboardService;
    private final UserRepository userRepository;
    private final FinancialGoalService financialGoalService;

    public DashboardController(
            DashboardService dashboardService,
            UserRepository userRepository,
            BudgetService budgetService,
        FinancialGoalService financialGoalService) {

        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
        this.budgetService = budgetService;
        this.financialGoalService = financialGoalService;
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

    @GetMapping("/goal-progress")
    public ResponseEntity<List<GoalProgressResponse>> getGoalProgress(
            Authentication authentication) {

        Long userId = getLoggedInUserId(authentication);

        List<FinancialGoalResponse> goals = financialGoalService.getAllGoalsForDashboard(userId);

        List<GoalProgressResponse> response = goals.stream()
                .map(goal -> new GoalProgressResponse(
                        goal.getId(),
                        goal.getName(),
                        goal.getTargetAmount(),
                        goal.getCurrentAmount(),
                        goal.getRemainingAmount(),
                        goal.getPercentageCompleted(),
                        goal.getTargetDate(),
                        goal.getStatus()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/financial-health")
    public ResponseEntity<FinancialHealthResponse> getFinancialHealth(
            Authentication authentication) {

        Long userId = getLoggedInUserId(authentication);

        FinancialHealthResponse response = dashboardService.getFinancialHealth(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<RecentActivityResponse>> getRecentActivity(
            Authentication authentication) {

        Long userId = getLoggedInUserId(authentication);

        List<RecentActivityResponse> response = dashboardService.getRecentActivity(userId);

        return ResponseEntity.ok(response);
    }
}
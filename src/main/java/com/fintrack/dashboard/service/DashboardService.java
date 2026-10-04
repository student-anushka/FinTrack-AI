package com.fintrack.dashboard.service;

import com.fintrack.budget.entity.Budget;
import com.fintrack.budget.repository.BudgetRepository;
import com.fintrack.dashboard.dto.CategorySpendingResponse;
import com.fintrack.dashboard.dto.DashboardSummaryResponse;
import com.fintrack.dashboard.dto.FinancialHealthResponse;
import com.fintrack.dashboard.dto.MonthlyTrendResponse;
import com.fintrack.expense.repository.ExpenseRepository;
import com.fintrack.goal.entity.FinancialGoal;
import com.fintrack.goal.repository.FinancialGoalRepository;
import com.fintrack.income.repository.IncomeRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.fintrack.expense.entity.Expense;
import com.fintrack.goal.entity.GoalContribution;
import com.fintrack.goal.repository.GoalContributionRepository;
import com.fintrack.income.entity.Income;
import com.fintrack.dashboard.dto.RecentActivityResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

        private final BudgetRepository budgetRepository;
        private final FinancialGoalRepository financialGoalRepository;
        private final IncomeRepository incomeRepository;
        private final ExpenseRepository expenseRepository;
        private final GoalContributionRepository goalContributionRepository;
        
        public DashboardService(
                        IncomeRepository incomeRepository,
                        ExpenseRepository expenseRepository,
                        BudgetRepository budgetRepository,
                        FinancialGoalRepository financialGoalRepository,
                        GoalContributionRepository goalContributionRepository) {

                this.incomeRepository = incomeRepository;
                this.expenseRepository = expenseRepository;
                this.budgetRepository = budgetRepository;
                this.financialGoalRepository = financialGoalRepository;
                this.goalContributionRepository = goalContributionRepository;
        }

        // =========================================================
        // DASHBOARD SUMMARY
        // =========================================================

        public DashboardSummaryResponse getSummary(Long userId) {

                BigDecimal totalIncome = incomeRepository.calculateTotalIncome(userId);

                BigDecimal totalExpense = expenseRepository.calculateTotalExpense(userId);

                BigDecimal totalSavings = totalIncome.subtract(totalExpense);

                BigDecimal savingsRate = BigDecimal.ZERO;

                if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {

                        savingsRate = totalSavings
                                        .divide(
                                                        totalIncome,
                                                        4,
                                                        RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100))
                                        .setScale(2, RoundingMode.HALF_UP);
                }

                LocalDate today = LocalDate.now();

                LocalDate monthStart = today.withDayOfMonth(1);

                LocalDate monthEnd = today.withDayOfMonth(
                                today.lengthOfMonth());

                BigDecimal currentMonthIncome = incomeRepository.calculateIncomeBetweenDates(
                                userId,
                                monthStart,
                                monthEnd);

                BigDecimal currentMonthExpense = expenseRepository.calculateExpenseBetweenDates(
                                userId,
                                monthStart,
                                monthEnd);

                return new DashboardSummaryResponse(
                                totalIncome,
                                totalExpense,
                                totalSavings,
                                savingsRate,
                                currentMonthIncome,
                                currentMonthExpense);
        }

        // =========================================================
        // CATEGORY-WISE SPENDING
        // =========================================================

        public List<CategorySpendingResponse> getCategoryWiseSpending(
                        Long userId) {

                List<Object[]> results = expenseRepository.findCategoryWiseExpense(userId);

                BigDecimal totalExpense = expenseRepository.calculateTotalExpense(userId);

                List<CategorySpendingResponse> response = new ArrayList<>();

                for (Object[] row : results) {

                        Long categoryId = ((Number) row[0]).longValue();

                        String categoryName = (String) row[1];

                        BigDecimal totalAmount = (BigDecimal) row[2];

                        BigDecimal percentage = BigDecimal.ZERO;

                        if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {

                                percentage = totalAmount
                                                .divide(
                                                                totalExpense,
                                                                4,
                                                                RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                                .setScale(2, RoundingMode.HALF_UP);
                        }

                        response.add(
                                        new CategorySpendingResponse(
                                                        categoryId,
                                                        categoryName,
                                                        totalAmount,
                                                        percentage));
                }

                return response;
        }

        // =========================================================
        // MONTHLY TRENDS
        // =========================================================

        public List<MonthlyTrendResponse> getMonthlyTrends(
                        Long userId) {

                List<Object[]> incomeResults = incomeRepository.findMonthlyIncome(userId);

                List<Object[]> expenseResults = expenseRepository.findMonthlyExpense(userId);

                Map<String, BigDecimal> monthlyIncome = new HashMap<>();

                Map<String, BigDecimal> monthlyExpense = new HashMap<>();

                for (Object[] row : incomeResults) {

                        Integer year = ((Number) row[0]).intValue();

                        Integer month = ((Number) row[1]).intValue();

                        BigDecimal amount = (BigDecimal) row[2];

                        String key = year + "-" + month;

                        monthlyIncome.put(key, amount);
                }

                for (Object[] row : expenseResults) {

                        Integer year = ((Number) row[0]).intValue();

                        Integer month = ((Number) row[1]).intValue();

                        BigDecimal amount = (BigDecimal) row[2];

                        String key = year + "-" + month;

                        monthlyExpense.put(key, amount);
                }

                Map<String, Boolean> months = new HashMap<>();

                for (Object[] row : incomeResults) {

                        Integer year = ((Number) row[0]).intValue();

                        Integer month = ((Number) row[1]).intValue();

                        months.put(
                                        year + "-" + month,
                                        true);
                }

                for (Object[] row : expenseResults) {

                        Integer year = ((Number) row[0]).intValue();

                        Integer month = ((Number) row[1]).intValue();

                        months.put(
                                        year + "-" + month,
                                        true);
                }

                List<MonthlyTrendResponse> response = new ArrayList<>();

                for (String key : months.keySet()) {

                        String[] parts = key.split("-");

                        Integer year = Integer.parseInt(parts[0]);

                        Integer month = Integer.parseInt(parts[1]);

                        BigDecimal income = monthlyIncome.getOrDefault(
                                        key,
                                        BigDecimal.ZERO);

                        BigDecimal expense = monthlyExpense.getOrDefault(
                                        key,
                                        BigDecimal.ZERO);

                        String monthName = Month.of(month).name();

                        response.add(
                                        new MonthlyTrendResponse(
                                                        year,
                                                        month,
                                                        monthName,
                                                        income,
                                                        expense));
                }

                response.sort(
                                (a, b) -> {

                                        int yearComparison = a.getYear()
                                                        .compareTo(b.getYear());

                                        if (yearComparison != 0) {
                                                return yearComparison;
                                        }

                                        return a.getMonth()
                                                        .compareTo(b.getMonth());
                                });

                return response;
        }

        // =========================================================
        // FINANCIAL HEALTH SCORE
        // =========================================================

        public FinancialHealthResponse getFinancialHealth(
                        Long userId) {

                BigDecimal totalIncome = incomeRepository.calculateTotalIncome(userId);

                BigDecimal totalExpense = expenseRepository.calculateTotalExpense(userId);

                // -----------------------------------------------------
                // 1. SAVINGS SCORE - 30 POINTS
                // -----------------------------------------------------

                BigDecimal savingsScore = BigDecimal.ZERO;

                if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {

                        BigDecimal savingsRate = totalIncome
                                        .subtract(totalExpense)
                                        .divide(
                                                        totalIncome,
                                                        4,
                                                        RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100));

                        if (savingsRate.compareTo(BigDecimal.ZERO) > 0) {

                                savingsScore = savingsRate
                                                .divide(
                                                                BigDecimal.valueOf(20),
                                                                4,
                                                                RoundingMode.HALF_UP)
                                                .multiply(
                                                                BigDecimal.valueOf(30));

                                if (savingsScore.compareTo(
                                                BigDecimal.valueOf(30)) > 0) {

                                        savingsScore = BigDecimal.valueOf(30);
                                }
                        }
                }

                // -----------------------------------------------------
                // 2. EXPENSE CONTROL SCORE - 25 POINTS
                // -----------------------------------------------------

                BigDecimal expenseControlScore = BigDecimal.ZERO;

                if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {

                        BigDecimal expenseRatio = totalExpense
                                        .divide(
                                                        totalIncome,
                                                        4,
                                                        RoundingMode.HALF_UP)
                                        .multiply(
                                                        BigDecimal.valueOf(100));

                        // Expense <= 50% of income
                        // Full 25 points
                        if (expenseRatio.compareTo(
                                        BigDecimal.valueOf(50)) <= 0) {

                                expenseControlScore = BigDecimal.valueOf(25);

                                // Expense between 50% and 100%
                        } else if (expenseRatio.compareTo(
                                        BigDecimal.valueOf(100)) < 0) {

                                BigDecimal reduction = expenseRatio
                                                .subtract(
                                                                BigDecimal.valueOf(50))
                                                .divide(
                                                                BigDecimal.valueOf(50),
                                                                4,
                                                                RoundingMode.HALF_UP)
                                                .multiply(
                                                                BigDecimal.valueOf(25));

                                expenseControlScore = BigDecimal.valueOf(25)
                                                .subtract(reduction);
                        }
                }

                // -----------------------------------------------------
                // 3. BUDGET SCORE - 25 POINTS
                // -----------------------------------------------------

                BigDecimal budgetScore = BigDecimal.ZERO;

                List<Budget> budgets = budgetRepository.findAllByUserId(userId);

                if (!budgets.isEmpty()) {

                        BigDecimal totalUtilization = BigDecimal.ZERO;

                        int validBudgets = 0;

                        for (Budget budget : budgets) {

                                BigDecimal spent = expenseRepository.calculateTotalSpent(
                                                userId,
                                                budget.getCategory().getId(),
                                                budget.getStartDate(),
                                                budget.getEndDate());

                                if (budget.getAmount()
                                                .compareTo(BigDecimal.ZERO) > 0) {

                                        BigDecimal utilization = spent
                                                        .divide(
                                                                        budget.getAmount(),
                                                                        4,
                                                                        RoundingMode.HALF_UP)
                                                        .multiply(
                                                                        BigDecimal.valueOf(100));

                                        totalUtilization = totalUtilization.add(
                                                        utilization);

                                        validBudgets++;
                                }
                        }

                        if (validBudgets > 0) {

                                BigDecimal averageUtilization = totalUtilization.divide(
                                                BigDecimal.valueOf(
                                                                validBudgets),
                                                4,
                                                RoundingMode.HALF_UP);

                                // <= 80% utilization
                                // Full 25 points
                                if (averageUtilization.compareTo(
                                                BigDecimal.valueOf(80)) <= 0) {

                                        budgetScore = BigDecimal.valueOf(25);

                                        // 80% - 120%
                                } else if (averageUtilization.compareTo(
                                                BigDecimal.valueOf(120)) < 0) {

                                        BigDecimal reduction = averageUtilization
                                                        .subtract(
                                                                        BigDecimal.valueOf(80))
                                                        .divide(
                                                                        BigDecimal.valueOf(40),
                                                                        4,
                                                                        RoundingMode.HALF_UP)
                                                        .multiply(
                                                                        BigDecimal.valueOf(25));

                                        budgetScore = BigDecimal.valueOf(25)
                                                        .subtract(reduction);
                                }
                        }
                }

                // -----------------------------------------------------
                // 4. GOAL SCORE - 20 POINTS
                // -----------------------------------------------------

                BigDecimal goalScore = BigDecimal.ZERO;

                List<FinancialGoal> goals = financialGoalRepository.findByUserId(userId);

                if (!goals.isEmpty()) {

                        BigDecimal totalCompletion = BigDecimal.ZERO;

                        int validGoals = 0;

                        for (FinancialGoal goal : goals) {

                                if (goal.getTargetAmount()
                                                .compareTo(BigDecimal.ZERO) > 0) {

                                        BigDecimal completion = goal.getCurrentAmount()
                                                        .divide(
                                                                        goal.getTargetAmount(),
                                                                        4,
                                                                        RoundingMode.HALF_UP)
                                                        .multiply(
                                                                        BigDecimal.valueOf(100));

                                        if (completion.compareTo(
                                                        BigDecimal.valueOf(100)) > 0) {

                                                completion = BigDecimal.valueOf(100);
                                        }

                                        totalCompletion = totalCompletion.add(
                                                        completion);

                                        validGoals++;
                                }
                        }

                        if (validGoals > 0) {

                                BigDecimal averageCompletion = totalCompletion.divide(
                                                BigDecimal.valueOf(validGoals),
                                                4,
                                                RoundingMode.HALF_UP);

                                goalScore = averageCompletion
                                                .divide(
                                                                BigDecimal.valueOf(100),
                                                                4,
                                                                RoundingMode.HALF_UP)
                                                .multiply(
                                                                BigDecimal.valueOf(20));
                        }
                }

                // -----------------------------------------------------
                // FINAL SCORE
                // -----------------------------------------------------

                BigDecimal totalScore = savingsScore
                                .add(expenseControlScore)
                                .add(budgetScore)
                                .add(goalScore)
                                .setScale(
                                                2,
                                                RoundingMode.HALF_UP);

                // -----------------------------------------------------
                // RATING
                // -----------------------------------------------------

                String rating;

                if (totalScore.compareTo(
                                BigDecimal.valueOf(80)) >= 0) {

                        rating = "EXCELLENT";

                } else if (totalScore.compareTo(
                                BigDecimal.valueOf(65)) >= 0) {

                        rating = "GOOD";

                } else if (totalScore.compareTo(
                                BigDecimal.valueOf(50)) >= 0) {

                        rating = "FAIR";

                } else {

                        rating = "NEEDS_IMPROVEMENT";
                }

                return new FinancialHealthResponse(
                                totalScore,
                                rating,
                                savingsScore.setScale(
                                                2,
                                                RoundingMode.HALF_UP),
                                expenseControlScore.setScale(
                                                2,
                                                RoundingMode.HALF_UP),
                                budgetScore.setScale(
                                                2,
                                                RoundingMode.HALF_UP),
                                goalScore.setScale(
                                                2,
                                                RoundingMode.HALF_UP));
        }

        public List<RecentActivityResponse> getRecentActivity(
                        Long userId) {

                List<RecentActivityResponse> activities = new ArrayList<>();

                // ---------------------------------------------------------
                // EXPENSES
                // ---------------------------------------------------------

                List<Expense> expenses = expenseRepository
                                .findTop10ByUserIdOrderByExpenseDateDesc(userId);

                for (Expense expense : expenses) {

                        activities.add(
                                        new RecentActivityResponse(
                                                        "EXPENSE",
                                                        expense.getTitle(),
                                                        expense.getAmount(),
                                                        expense.getExpenseDate()));
                }

                // ---------------------------------------------------------
                // INCOME
                // ---------------------------------------------------------

                List<Income> incomes = incomeRepository
                                .findTop10ByUserIdOrderByIncomeDateDesc(userId);

                for (Income income : incomes) {

                        activities.add(
                                        new RecentActivityResponse(
                                                        "INCOME",
                                                        income.getSource(),
                                                        income.getAmount(),
                                                        income.getIncomeDate()));
                }

                // ---------------------------------------------------------
                // GOAL CONTRIBUTIONS
                // ---------------------------------------------------------

                List<GoalContribution> contributions = goalContributionRepository
                                .findTop10ByGoalUserIdOrderByContributionDateDesc(
                                                userId);

                for (GoalContribution contribution : contributions) {

                        String title = contribution.getGoal().getName();

                        activities.add(
                                        new RecentActivityResponse(
                                                        "GOAL_CONTRIBUTION",
                                                        title,
                                                        contribution.getAmount(),
                                                        contribution.getContributionDate()));
                }

                // ---------------------------------------------------------
                // SORT ALL ACTIVITIES
                // ---------------------------------------------------------

                activities.sort(
                                (a, b) -> b.getDate().compareTo(a.getDate()));

                // ---------------------------------------------------------
                // RETURN ONLY LATEST 10
                // ---------------------------------------------------------

                if (activities.size() > 10) {

                        return new ArrayList<>(
                                        activities.subList(0, 10));
                }

                return activities;
        }
}
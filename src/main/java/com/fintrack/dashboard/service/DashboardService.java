package com.fintrack.dashboard.service;

import com.fintrack.dashboard.dto.CategorySpendingResponse;
import com.fintrack.dashboard.dto.DashboardSummaryResponse;
import com.fintrack.dashboard.dto.MonthlyTrendResponse;
import com.fintrack.expense.repository.ExpenseRepository;
import com.fintrack.income.repository.IncomeRepository;
import org.springframework.stereotype.Service;

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

        private final IncomeRepository incomeRepository;
        private final ExpenseRepository expenseRepository;

        public DashboardService(
                        IncomeRepository incomeRepository,
                        ExpenseRepository expenseRepository) {

                this.incomeRepository = incomeRepository;
                this.expenseRepository = expenseRepository;
        }

        public DashboardSummaryResponse getSummary(Long userId) {

                BigDecimal totalIncome = incomeRepository.calculateTotalIncome(userId);

                BigDecimal totalExpense = expenseRepository.calculateTotalExpense(userId);

                BigDecimal totalSavings = totalIncome.subtract(totalExpense);

                BigDecimal savingsRate = BigDecimal.ZERO;

                if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {

                        savingsRate = totalSavings
                                        .divide(totalIncome, 4, RoundingMode.HALF_UP)
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
                                                .divide(totalExpense, 4, RoundingMode.HALF_UP)
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

                        months.put(year + "-" + month, true);
                }

                for (Object[] row : expenseResults) {

                        Integer year = ((Number) row[0]).intValue();

                        Integer month = ((Number) row[1]).intValue();

                        months.put(year + "-" + month, true);
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

                                        int yearComparison = a.getYear().compareTo(b.getYear());

                                        if (yearComparison != 0) {
                                                return yearComparison;
                                        }

                                        return a.getMonth()
                                                        .compareTo(b.getMonth());
                                });

                return response;
        }
}
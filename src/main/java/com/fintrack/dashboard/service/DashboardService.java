package com.fintrack.dashboard.service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.dashboard.dto.DashboardSummaryResponse;
import com.fintrack.expense.repository.ExpenseRepository;
import com.fintrack.income.repository.IncomeRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class DashboardService {

        private final UserRepository userRepository;
        private final IncomeRepository incomeRepository;
        private final ExpenseRepository expenseRepository;

        public DashboardService(
                        UserRepository userRepository,
                        IncomeRepository incomeRepository,
                        ExpenseRepository expenseRepository) {

                this.userRepository = userRepository;
                this.incomeRepository = incomeRepository;
                this.expenseRepository = expenseRepository;
        }

        public DashboardSummaryResponse getSummary(
                        Authentication authentication) {

                User user = userRepository
                                .findByEmail(authentication.getName())
                                .orElseThrow(() -> new RuntimeException(
                                                "Authenticated user not found"));

                Long userId = user.getId();

                BigDecimal totalIncome = incomeRepository.calculateTotalIncome(userId);

                BigDecimal totalExpense = expenseRepository.calculateTotalExpense(userId);

                BigDecimal totalSavings = totalIncome.subtract(totalExpense);

                BigDecimal savingsRate = calculateSavingsRate(
                                totalIncome,
                                totalSavings);

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

        private BigDecimal calculateSavingsRate(
                        BigDecimal totalIncome,
                        BigDecimal totalSavings) {

                if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
                        return BigDecimal.ZERO;
                }

                return totalSavings
                                .multiply(BigDecimal.valueOf(100))
                                .divide(
                                                totalIncome,
                                                2,
                                                RoundingMode.HALF_UP);
        }
}
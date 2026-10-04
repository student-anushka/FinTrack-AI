package com.fintrack.dashboard.dto;

import java.math.BigDecimal;

public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalSavings;
    private BigDecimal savingsRate;

    private BigDecimal totalBudget;
    private BigDecimal totalBudgetSpent;
    private BigDecimal budgetUsagePercentage;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal totalSavings,
            BigDecimal savingsRate,
            BigDecimal totalBudget,
            BigDecimal totalBudgetSpent,
            BigDecimal budgetUsagePercentage) {

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.totalSavings = totalSavings;
        this.savingsRate = savingsRate;
        this.totalBudget = totalBudget;
        this.totalBudgetSpent = totalBudgetSpent;
        this.budgetUsagePercentage = budgetUsagePercentage;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getTotalSavings() {
        return totalSavings;
    }

    public BigDecimal getSavingsRate() {
        return savingsRate;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public BigDecimal getTotalBudgetSpent() {
        return totalBudgetSpent;
    }

    public BigDecimal getBudgetUsagePercentage() {
        return budgetUsagePercentage;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public void setTotalSavings(BigDecimal totalSavings) {
        this.totalSavings = totalSavings;
    }

    public void setSavingsRate(BigDecimal savingsRate) {
        this.savingsRate = savingsRate;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public void setTotalBudgetSpent(BigDecimal totalBudgetSpent) {
        this.totalBudgetSpent = totalBudgetSpent;
    }

    public void setBudgetUsagePercentage(
            BigDecimal budgetUsagePercentage) {

        this.budgetUsagePercentage = budgetUsagePercentage;
    }
}
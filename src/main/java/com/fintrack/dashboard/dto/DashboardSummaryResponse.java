package com.fintrack.dashboard.dto;

import java.math.BigDecimal;

public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalSavings;
    private BigDecimal savingsRate;

    private BigDecimal currentMonthIncome;
    private BigDecimal currentMonthExpense;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal totalSavings,
            BigDecimal savingsRate,
            BigDecimal currentMonthIncome,
            BigDecimal currentMonthExpense) {

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.totalSavings = totalSavings;
        this.savingsRate = savingsRate;
        this.currentMonthIncome = currentMonthIncome;
        this.currentMonthExpense = currentMonthExpense;
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

    public BigDecimal getCurrentMonthIncome() {
        return currentMonthIncome;
    }

    public BigDecimal getCurrentMonthExpense() {
        return currentMonthExpense;
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

    public void setCurrentMonthIncome(
            BigDecimal currentMonthIncome) {

        this.currentMonthIncome = currentMonthIncome;
    }

    public void setCurrentMonthExpense(
            BigDecimal currentMonthExpense) {

        this.currentMonthExpense = currentMonthExpense;
    }
}
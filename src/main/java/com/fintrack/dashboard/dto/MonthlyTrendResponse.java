package com.fintrack.dashboard.dto;

import java.math.BigDecimal;

public class MonthlyTrendResponse {

    private Integer year;
    private Integer month;
    private String monthName;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalSavings;

    public MonthlyTrendResponse(
            Integer year,
            Integer month,
            String monthName,
            BigDecimal totalIncome,
            BigDecimal totalExpense) {

        this.year = year;
        this.month = month;
        this.monthName = monthName;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.totalSavings = totalIncome.subtract(totalExpense);
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public String getMonthName() {
        return monthName;
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
}
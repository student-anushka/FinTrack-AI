package com.fintrack.dashboard.dto;

import java.math.BigDecimal;

public class FinancialHealthResponse {

    private BigDecimal score;
    private String rating;

    private BigDecimal savingsScore;
    private BigDecimal expenseControlScore;
    private BigDecimal budgetScore;
    private BigDecimal goalScore;

    public FinancialHealthResponse(
            BigDecimal score,
            String rating,
            BigDecimal savingsScore,
            BigDecimal expenseControlScore,
            BigDecimal budgetScore,
            BigDecimal goalScore) {

        this.score = score;
        this.rating = rating;
        this.savingsScore = savingsScore;
        this.expenseControlScore = expenseControlScore;
        this.budgetScore = budgetScore;
        this.goalScore = goalScore;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getRating() {
        return rating;
    }

    public BigDecimal getSavingsScore() {
        return savingsScore;
    }

    public BigDecimal getExpenseControlScore() {
        return expenseControlScore;
    }

    public BigDecimal getBudgetScore() {
        return budgetScore;
    }

    public BigDecimal getGoalScore() {
        return goalScore;
    }
}
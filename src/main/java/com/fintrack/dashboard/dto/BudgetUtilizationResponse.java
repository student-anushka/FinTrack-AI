package com.fintrack.dashboard.dto;

import java.math.BigDecimal;

public class BudgetUtilizationResponse {

    private Long budgetId;
    private String budgetName;
    private String categoryName;

    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageUsed;

    private String status;

    public BudgetUtilizationResponse(
            Long budgetId,
            String budgetName,
            String categoryName,
            BigDecimal budgetAmount,
            BigDecimal spentAmount,
            BigDecimal remainingAmount,
            BigDecimal percentageUsed,
            String status) {

        this.budgetId = budgetId;
        this.budgetName = budgetName;
        this.categoryName = categoryName;
        this.budgetAmount = budgetAmount;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.percentageUsed = percentageUsed;
        this.status = status;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public BigDecimal getPercentageUsed() {
        return percentageUsed;
    }

    public String getStatus() {
        return status;
    }
}
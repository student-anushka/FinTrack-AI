package com.fintrack.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetResponse {

    private Long id;
    private String name;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long categoryId;
    private String categoryName;

    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageUsed;
    private String status;

    public BudgetResponse() {
    }

    public BudgetResponse(
            Long id,
            String name,
            BigDecimal amount,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            String categoryName,
            BigDecimal spentAmount,
            BigDecimal remainingAmount,
            BigDecimal percentageUsed,
            String status) {

        this.id = id;
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.percentageUsed = percentageUsed;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getPercentageUsed() {
        return percentageUsed;
    }

    public void setPercentageUsed(BigDecimal percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
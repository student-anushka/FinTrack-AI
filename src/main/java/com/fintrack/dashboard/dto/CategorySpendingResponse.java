package com.fintrack.dashboard.dto;

import java.math.BigDecimal;

public class CategorySpendingResponse {

    private Long categoryId;
    private String categoryName;
    private BigDecimal totalAmount;
    private BigDecimal percentage;

    public CategorySpendingResponse() {
    }

    public CategorySpendingResponse(
            Long categoryId,
            String categoryName,
            BigDecimal totalAmount,
            BigDecimal percentage) {

        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}
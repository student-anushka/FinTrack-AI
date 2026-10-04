package com.fintrack.goal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialGoalResponse {

    private Long id;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageCompleted;
    private LocalDate targetDate;
    private String description;
    private String status;

    public FinancialGoalResponse() {
    }

    public FinancialGoalResponse(
            Long id,
            String name,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            BigDecimal remainingAmount,
            BigDecimal percentageCompleted,
            LocalDate targetDate,
            String description,
            String status) {

        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.remainingAmount = remainingAmount;
        this.percentageCompleted = percentageCompleted;
        this.targetDate = targetDate;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public BigDecimal getPercentageCompleted() {
        return percentageCompleted;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public void setPercentageCompleted(BigDecimal percentageCompleted) {
        this.percentageCompleted = percentageCompleted;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
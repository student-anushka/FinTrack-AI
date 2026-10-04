package com.fintrack.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalProgressResponse {

    private Long goalId;
    private String goalName;

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageCompleted;

    private LocalDate targetDate;
    private String status;

    public GoalProgressResponse(
            Long goalId,
            String goalName,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            BigDecimal remainingAmount,
            BigDecimal percentageCompleted,
            LocalDate targetDate,
            String status) {

        this.goalId = goalId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.remainingAmount = remainingAmount;
        this.percentageCompleted = percentageCompleted;
        this.targetDate = targetDate;
        this.status = status;
    }

    public Long getGoalId() {
        return goalId;
    }

    public String getGoalName() {
        return goalName;
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

    public String getStatus() {
        return status;
    }
}
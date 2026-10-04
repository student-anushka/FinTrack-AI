package com.fintrack.goal.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalContributionResponse {

    private Long id;
    private BigDecimal amount;
    private LocalDate contributionDate;
    private String note;

    public GoalContributionResponse() {
    }

    public GoalContributionResponse(
            Long id,
            BigDecimal amount,
            LocalDate contributionDate,
            String note) {

        this.id = id;
        this.amount = amount;
        this.contributionDate = contributionDate;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getContributionDate() {
        return contributionDate;
    }

    public String getNote() {
        return note;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setContributionDate(LocalDate contributionDate) {
        this.contributionDate = contributionDate;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
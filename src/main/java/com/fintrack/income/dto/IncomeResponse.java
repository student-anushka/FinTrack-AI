package com.fintrack.income.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeResponse {

    private Long id;
    private String source;
    private BigDecimal amount;
    private String description;
    private LocalDate incomeDate;
    private String incomeType;

    public IncomeResponse() {
    }

    public IncomeResponse(
            Long id,
            String source,
            BigDecimal amount,
            String description,
            LocalDate incomeDate,
            String incomeType) {

        this.id = id;
        this.source = source;
        this.amount = amount;
        this.description = description;
        this.incomeDate = incomeDate;
        this.incomeType = incomeType;
    }

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getIncomeDate() {
        return incomeDate;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIncomeDate(LocalDate incomeDate) {
        this.incomeDate = incomeDate;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }
}
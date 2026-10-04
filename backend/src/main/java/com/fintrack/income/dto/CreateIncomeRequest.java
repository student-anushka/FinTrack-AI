package com.fintrack.income.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateIncomeRequest {

    @NotBlank(message = "Source is required")
    private String source;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Income date is required")
    private LocalDate incomeDate;

    @NotBlank(message = "Income type is required")
    private String incomeType;

    public CreateIncomeRequest() {
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
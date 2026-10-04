package com.fintrack.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecentActivityResponse {

    private String type;
    private String title;
    private BigDecimal amount;
    private LocalDate date;

    public RecentActivityResponse(
            String type,
            String title,
            BigDecimal amount,
            LocalDate date) {

        this.type = type;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}
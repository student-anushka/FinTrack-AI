package com.fintrack.dashboard.dto;

public record FinancialInsightResponse(
        String type,
        String title,
        String message,
        String severity) {
}
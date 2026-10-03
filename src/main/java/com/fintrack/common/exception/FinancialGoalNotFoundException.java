package com.fintrack.common.exception;

public class FinancialGoalNotFoundException extends RuntimeException {

    public FinancialGoalNotFoundException(String message) {
        super(message);
    }
}
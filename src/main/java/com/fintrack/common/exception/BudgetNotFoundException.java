package com.fintrack.common.exception;

public class BudgetNotFoundException extends RuntimeException {

    public BudgetNotFoundException(String message) {
        super(message);
    }
}
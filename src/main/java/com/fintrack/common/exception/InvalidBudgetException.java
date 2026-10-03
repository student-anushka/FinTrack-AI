package com.fintrack.common.exception;

public class InvalidBudgetException extends RuntimeException {

    public InvalidBudgetException(String message) {
        super(message);
    }
}
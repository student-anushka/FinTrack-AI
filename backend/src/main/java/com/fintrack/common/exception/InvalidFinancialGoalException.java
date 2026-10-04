package com.fintrack.common.exception;

public class InvalidFinancialGoalException extends RuntimeException {

    public InvalidFinancialGoalException(String message) {
        super(message);
    }
}
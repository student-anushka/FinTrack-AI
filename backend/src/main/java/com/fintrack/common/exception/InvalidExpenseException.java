package com.fintrack.common.exception;

public class InvalidExpenseException extends RuntimeException {

    public InvalidExpenseException(String message) {
        super(message);
    }
}
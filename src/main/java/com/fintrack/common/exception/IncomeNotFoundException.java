package com.fintrack.common.exception;

public class IncomeNotFoundException extends RuntimeException {

    public IncomeNotFoundException(String message) {
        super(message);
    }
}
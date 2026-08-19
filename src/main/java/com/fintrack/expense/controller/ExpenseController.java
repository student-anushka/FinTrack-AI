package com.fintrack.expense.controller;

import com.fintrack.expense.dto.CreateExpenseRequest;
import com.fintrack.expense.dto.ExpenseResponse;
import com.fintrack.expense.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication) {

        ExpenseResponse response = expenseService.createExpense(
                request,
                authentication);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }
}
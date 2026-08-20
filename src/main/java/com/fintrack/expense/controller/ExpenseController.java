package com.fintrack.expense.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintrack.expense.dto.CreateExpenseRequest;
import com.fintrack.expense.dto.ExpenseResponse;
import com.fintrack.expense.service.ExpenseService;
import java.util.List;
import jakarta.validation.Valid;

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

    @GetMapping
public ResponseEntity<List<ExpenseResponse>> getMyExpenses(
        Authentication authentication) {

    List<ExpenseResponse> expenses =
            expenseService.getMyExpenses(authentication);

    return ResponseEntity.ok(expenses);
}
}
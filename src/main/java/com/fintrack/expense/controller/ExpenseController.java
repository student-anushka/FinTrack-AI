package com.fintrack.expense.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintrack.expense.dto.CreateExpenseRequest;
import com.fintrack.expense.dto.ExpenseResponse;
import com.fintrack.expense.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // CREATE
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

    // GET ALL MY EXPENSES
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getMyExpenses(
            Authentication authentication) {

        List<ExpenseResponse> expenses = expenseService.getMyExpenses(authentication);

        return ResponseEntity.ok(expenses);
    }

    // GET BY ID
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @PathVariable Long expenseId,
            Authentication authentication) {

        ExpenseResponse response = expenseService.getExpenseById(
                expenseId,
                authentication);

        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication) {

        ExpenseResponse response = expenseService.updateExpense(
                expenseId,
                request,
                authentication);

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long expenseId,
            Authentication authentication) {

        expenseService.deleteExpense(
                expenseId,
                authentication);

        return ResponseEntity.noContent().build();
    }
}
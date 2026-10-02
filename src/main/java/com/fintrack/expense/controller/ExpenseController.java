package com.fintrack.expense.controller;

import com.fintrack.expense.dto.CreateExpenseRequest;
import com.fintrack.expense.dto.ExpensePageResponse;
import com.fintrack.expense.dto.ExpenseResponse;
import com.fintrack.expense.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(
            ExpenseService expenseService) {

        this.expenseService = expenseService;
    }

    // =========================
    // CREATE
    // =========================

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

    // =========================
    // GET / SEARCH / FILTER
    // =========================

    @GetMapping
    public ResponseEntity<ExpensePageResponse> getMyExpenses(

            Authentication authentication,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "expenseDate") String sortBy,

            @RequestParam(defaultValue = "desc") String direction,

            @RequestParam(required = false) Long categoryId,

            @RequestParam(required = false) LocalDate startDate,

            @RequestParam(required = false) LocalDate endDate,

            @RequestParam(required = false) BigDecimal minAmount,

            @RequestParam(required = false) BigDecimal maxAmount,

            @RequestParam(required = false) String search) {

        ExpensePageResponse response = expenseService.filterExpenses(
                authentication,
                categoryId,
                startDate,
                endDate,
                minAmount,
                maxAmount,
                search,
                page,
                size,
                sortBy,
                direction);

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET BY ID
    // =========================

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @PathVariable Long expenseId,
            Authentication authentication) {

        ExpenseResponse response = expenseService.getExpenseById(
                expenseId,
                authentication);

        return ResponseEntity.ok(response);
    }

    // =========================
    // UPDATE
    // =========================

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

    // =========================
    // DELETE
    // =========================

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
package com.fintrack.budget.controller;

import com.fintrack.budget.dto.BudgetResponse;
import com.fintrack.budget.dto.CreateBudgetRequest;
import com.fintrack.budget.service.BudgetService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(
            BudgetService budgetService) {

        this.budgetService = budgetService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody
            CreateBudgetRequest request,
            Authentication authentication) {

        BudgetResponse response =
                budgetService.createBudget(
                        request,
                        authentication
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getMyBudgets(
            Authentication authentication) {

        List<BudgetResponse> budgets =
                budgetService.getMyBudgets(
                        authentication
                );

        return ResponseEntity.ok(budgets);
    }

    // GET BY ID
    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long budgetId,
            Authentication authentication) {

        BudgetResponse response =
                budgetService.getBudgetById(
                        budgetId,
                        authentication
                );

        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long budgetId,

            @Valid @RequestBody
            CreateBudgetRequest request,

            Authentication authentication) {

        BudgetResponse response =
                budgetService.updateBudget(
                        budgetId,
                        request,
                        authentication
                );

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long budgetId,
            Authentication authentication) {

        budgetService.deleteBudget(
                budgetId,
                authentication
        );

        return ResponseEntity.noContent().build();
    }
}
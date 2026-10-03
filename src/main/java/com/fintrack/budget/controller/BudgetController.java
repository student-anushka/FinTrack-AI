package com.fintrack.budget.controller;

import com.fintrack.budget.dto.BudgetPageResponse;
import com.fintrack.budget.dto.BudgetResponse;
import com.fintrack.budget.dto.CreateBudgetRequest;
import com.fintrack.budget.service.BudgetService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
                        @Valid @RequestBody CreateBudgetRequest request,
                        Authentication authentication) {

                BudgetResponse response = budgetService.createBudget(
                                request,
                                authentication);

                return new ResponseEntity<>(
                                response,
                                HttpStatus.CREATED);
        }

        // GET / SEARCH / FILTER
        @GetMapping
        public ResponseEntity<BudgetPageResponse> getMyBudgets(

                        Authentication authentication,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size,

                        @RequestParam(defaultValue = "startDate") String sortBy,

                        @RequestParam(defaultValue = "desc") String direction,

                        @RequestParam(required = false) Long categoryId,

                        @RequestParam(required = false) LocalDate startDate,

                        @RequestParam(required = false) LocalDate endDate,

                        @RequestParam(required = false) String search) {

                BudgetPageResponse response = budgetService.filterBudgets(
                                authentication,
                                categoryId,
                                startDate,
                                endDate,
                                search,
                                page,
                                size,
                                sortBy,
                                direction);

                return ResponseEntity.ok(response);
        }

        // GET BY ID
        @GetMapping("/{budgetId}")
        public ResponseEntity<BudgetResponse> getBudgetById(
                        @PathVariable Long budgetId,
                        Authentication authentication) {

                BudgetResponse response = budgetService.getBudgetById(
                                budgetId,
                                authentication);

                return ResponseEntity.ok(response);
        }

        // UPDATE
        @PutMapping("/{budgetId}")
        public ResponseEntity<BudgetResponse> updateBudget(
                        @PathVariable Long budgetId,
                        @Valid @RequestBody CreateBudgetRequest request,
                        Authentication authentication) {

                BudgetResponse response = budgetService.updateBudget(
                                budgetId,
                                request,
                                authentication);

                return ResponseEntity.ok(response);
        }

        // DELETE
        @DeleteMapping("/{budgetId}")
        public ResponseEntity<Void> deleteBudget(
                        @PathVariable Long budgetId,
                        Authentication authentication) {

                budgetService.deleteBudget(
                                budgetId,
                                authentication);

                return ResponseEntity.noContent().build();
        }
}
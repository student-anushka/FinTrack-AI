package com.fintrack.income.controller;

import com.fintrack.income.dto.CreateIncomeRequest;
import com.fintrack.income.dto.IncomeResponse;
import com.fintrack.income.service.IncomeService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping
    public ResponseEntity<IncomeResponse> createIncome(
            @Valid @RequestBody CreateIncomeRequest request,
            Authentication authentication) {

        IncomeResponse response = incomeService.createIncome(
                request,
                authentication);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @GetMapping("/{incomeId}")
    public ResponseEntity<IncomeResponse> getIncomeById(
            @PathVariable Long incomeId,
            Authentication authentication) {

        IncomeResponse response = incomeService.getIncomeById(
                incomeId,
                authentication);

        return ResponseEntity.ok(response);
    }
}
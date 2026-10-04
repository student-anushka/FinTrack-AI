package com.fintrack.income.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fintrack.income.dto.CreateIncomeRequest;
import com.fintrack.income.dto.IncomePageResponse;
import com.fintrack.income.dto.IncomeResponse;
import com.fintrack.income.service.IncomeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/incomes")
public class IncomeController {

        private final IncomeService incomeService;

        public IncomeController(
                        IncomeService incomeService) {

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

        @GetMapping
        public ResponseEntity<IncomePageResponse> getMyIncomes(

                        Authentication authentication,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size,

                        @RequestParam(defaultValue = "incomeDate") String sortBy,

                        @RequestParam(defaultValue = "desc") String direction,

                        @RequestParam(required = false) String incomeType,

                        @RequestParam(required = false) LocalDate startDate,

                        @RequestParam(required = false) LocalDate endDate,

                        @RequestParam(required = false) BigDecimal minAmount,

                        @RequestParam(required = false) BigDecimal maxAmount,

                        @RequestParam(required = false) String search) {

                IncomePageResponse response = incomeService.filterIncomes(
                                authentication,
                                incomeType,
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

        @GetMapping("/{incomeId}")
        public ResponseEntity<IncomeResponse> getIncomeById(
                        @PathVariable Long incomeId,
                        Authentication authentication) {

                IncomeResponse response = incomeService.getIncomeById(
                                incomeId,
                                authentication);

                return ResponseEntity.ok(response);
        }

        @PutMapping("/{incomeId}")
        public ResponseEntity<IncomeResponse> updateIncome(
                        @PathVariable Long incomeId,

                        @Valid @RequestBody CreateIncomeRequest request,

                        Authentication authentication) {

                IncomeResponse response = incomeService.updateIncome(
                                incomeId,
                                request,
                                authentication);

                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{incomeId}")
        public ResponseEntity<Void> deleteIncome(
                        @PathVariable Long incomeId,
                        Authentication authentication) {

                incomeService.deleteIncome(
                                incomeId,
                                authentication);

                return ResponseEntity.noContent().build();
        }
}
package com.fintrack.goal.controller;

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

import com.fintrack.goal.dto.CreateContributionRequest;
import com.fintrack.goal.dto.CreateFinancialGoalRequest;
import com.fintrack.goal.dto.FinancialGoalResponse;
import com.fintrack.goal.dto.UpdateFinancialGoalRequest;
import com.fintrack.goal.service.FinancialGoalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/goals")
public class FinancialGoalController {

        private final FinancialGoalService goalService;

        public FinancialGoalController(
                        FinancialGoalService goalService) {

                this.goalService = goalService;
        }

        @PostMapping
        public ResponseEntity<FinancialGoalResponse> createGoal(
                        @Valid @RequestBody CreateFinancialGoalRequest request,

                        Authentication authentication) {

                FinancialGoalResponse response = goalService.createGoal(
                                request,
                                authentication);

                return new ResponseEntity<>(
                                response,
                                HttpStatus.CREATED);
        }

        @GetMapping
        public ResponseEntity<List<FinancialGoalResponse>> getMyGoals(
                        Authentication authentication) {

                return ResponseEntity.ok(
                                goalService.getMyGoals(authentication));
        }

        @GetMapping("/{goalId}")
        public ResponseEntity<FinancialGoalResponse> getGoalById(
                        @PathVariable Long goalId,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                goalService.getGoalById(
                                                goalId,
                                                authentication));
        }

        @PutMapping("/{goalId}")
        public ResponseEntity<FinancialGoalResponse> updateGoal(
                        @PathVariable Long goalId,

                        @Valid @RequestBody UpdateFinancialGoalRequest request,

                        Authentication authentication) {

                return ResponseEntity.ok(
                                goalService.updateGoal(
                                                goalId,
                                                request,
                                                authentication));
        }

        @DeleteMapping("/{goalId}")
        public ResponseEntity<Void> deleteGoal(
                        @PathVariable Long goalId,
                        Authentication authentication) {

                goalService.deleteGoal(
                                goalId,
                                authentication);

                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{goalId}/contributions")
        public ResponseEntity<FinancialGoalResponse> addContribution(
                        @PathVariable Long goalId,

                        @Valid @RequestBody CreateContributionRequest request,

                        Authentication authentication) {

                return ResponseEntity.ok(
                                goalService.addContribution(
                                                goalId,
                                                request,
                                                authentication));
        }
}
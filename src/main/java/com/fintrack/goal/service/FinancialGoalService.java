package com.fintrack.goal.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.common.exception.FinancialGoalNotFoundException;
import com.fintrack.common.exception.InvalidFinancialGoalException;
import com.fintrack.goal.dto.CreateFinancialGoalRequest;
import com.fintrack.goal.dto.FinancialGoalResponse;
import com.fintrack.goal.entity.FinancialGoal;
import com.fintrack.goal.repository.FinancialGoalRepository;

@Service
public class FinancialGoalService {

    private final FinancialGoalRepository goalRepository;
    private final UserRepository userRepository;

    public FinancialGoalService(
            FinancialGoalRepository goalRepository,
            UserRepository userRepository) {

        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public FinancialGoalResponse createGoal(
            CreateFinancialGoalRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        validateTargetDate(request.getTargetDate());

        FinancialGoal goal = new FinancialGoal();

        goal.setName(request.getName().trim());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setTargetDate(request.getTargetDate());
        goal.setDescription(request.getDescription());
        goal.setUser(user);

        LocalDateTime now = LocalDateTime.now();

        goal.setCreatedAt(now);
        goal.setUpdatedAt(now);

        FinancialGoal savedGoal = goalRepository.save(goal);

        return mapToResponse(savedGoal);
    }

    public List<FinancialGoalResponse> getMyGoals(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return goalRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public FinancialGoalResponse getGoalById(
            Long goalId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        FinancialGoal goal = goalRepository
                .findByIdAndUserId(
                        goalId,
                        user.getId())
                .orElseThrow(() -> new FinancialGoalNotFoundException(
                        "Financial goal not found with id: "
                                + goalId));

        return mapToResponse(goal);
    }

    public FinancialGoalResponse updateGoal(
            Long goalId,
            CreateFinancialGoalRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        FinancialGoal goal = goalRepository
                .findByIdAndUserId(
                        goalId,
                        user.getId())
                .orElseThrow(() -> new FinancialGoalNotFoundException(
                        "Financial goal not found with id: "
                                + goalId));

        validateTargetDate(request.getTargetDate());

        goal.setName(request.getName().trim());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setDescription(request.getDescription());
        goal.setUpdatedAt(LocalDateTime.now());

        FinancialGoal updatedGoal = goalRepository.save(goal);

        return mapToResponse(updatedGoal);
    }

    public void deleteGoal(
            Long goalId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        FinancialGoal goal = goalRepository
                .findByIdAndUserId(
                        goalId,
                        user.getId())
                .orElseThrow(() -> new FinancialGoalNotFoundException(
                        "Financial goal not found with id: "
                                + goalId));

        goalRepository.delete(goal);
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Authenticated user not found"));
    }

    private void validateTargetDate(
            LocalDate targetDate) {

        if (targetDate.isBefore(LocalDate.now())) {

            throw new InvalidFinancialGoalException(
                    "Target date cannot be in the past");
        }
    }

    private FinancialGoalResponse mapToResponse(
            FinancialGoal goal) {

        BigDecimal current = goal.getCurrentAmount();

        BigDecimal target = goal.getTargetAmount();

        BigDecimal remaining = target.subtract(current);

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        BigDecimal percentage = current
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        target,
                        2,
                        RoundingMode.HALF_UP);

        if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            percentage = BigDecimal.valueOf(100);
        }

        String status;

        if (current.compareTo(target) >= 0) {
            status = "COMPLETED";
        } else if (goal.getTargetDate()
                .isBefore(LocalDate.now())) {
            status = "OVERDUE";
        } else {
            status = "IN_PROGRESS";
        }

        return new FinancialGoalResponse(
                goal.getId(),
                goal.getName(),
                target,
                current,
                remaining,
                percentage,
                goal.getTargetDate(),
                goal.getDescription(),
                status);
    }
}
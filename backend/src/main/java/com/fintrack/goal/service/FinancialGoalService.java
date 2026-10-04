package com.fintrack.goal.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fintrack.goal.dto.GoalContributionPageResponse;
import com.fintrack.goal.dto.GoalContributionResponse;
import com.fintrack.goal.specification.GoalContributionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.common.exception.FinancialGoalNotFoundException;
import com.fintrack.common.exception.InvalidFinancialGoalException;
import com.fintrack.goal.dto.CreateContributionRequest;
import com.fintrack.goal.dto.CreateFinancialGoalRequest;
import com.fintrack.goal.dto.FinancialGoalResponse;
import com.fintrack.goal.dto.UpdateFinancialGoalRequest;
import com.fintrack.goal.entity.FinancialGoal;
import com.fintrack.goal.entity.GoalContribution;
import com.fintrack.goal.repository.FinancialGoalRepository;
import com.fintrack.goal.repository.GoalContributionRepository;


@Service
public class FinancialGoalService {

        private final FinancialGoalRepository goalRepository;
        private final GoalContributionRepository contributionRepository;
        private final UserRepository userRepository;

        public FinancialGoalService(
                        FinancialGoalRepository goalRepository,
                        GoalContributionRepository contributionRepository,
                        UserRepository userRepository) {

                this.goalRepository = goalRepository;
                this.contributionRepository = contributionRepository;
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

                FinancialGoal goal = getOwnedGoal(goalId, authentication);

                return mapToResponse(goal);
        }

        public FinancialGoalResponse updateGoal(
                        Long goalId,
                        UpdateFinancialGoalRequest request,
                        Authentication authentication) {

                FinancialGoal goal = getOwnedGoal(goalId, authentication);

                validateTargetDate(request.getTargetDate());

                if (request.getTargetAmount()
                                .compareTo(goal.getCurrentAmount()) < 0) {

                        throw new InvalidFinancialGoalException(
                                        "Target amount cannot be less than current saved amount");
                }

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

                FinancialGoal goal = getOwnedGoal(goalId, authentication);

                goalRepository.delete(goal);
        }

        public FinancialGoalResponse addContribution(
                        Long goalId,
                        CreateContributionRequest request,
                        Authentication authentication) {

                FinancialGoal goal = getOwnedGoal(goalId, authentication);

                validateContributionDate(
                                request.getContributionDate());

                BigDecimal newCurrentAmount = goal.getCurrentAmount()
                                .add(request.getAmount());

                if (newCurrentAmount.compareTo(
                                goal.getTargetAmount()) > 0) {

                        throw new InvalidFinancialGoalException(
                                        "Contribution exceeds the remaining goal amount");
                }

                GoalContribution contribution = new GoalContribution();

                contribution.setAmount(
                                request.getAmount());

                contribution.setContributionDate(
                                request.getContributionDate());

                contribution.setNote(
                                request.getNote());

                contribution.setGoal(goal);

                contribution.setCreatedAt(
                                LocalDateTime.now());

                contributionRepository.save(contribution);

                goal.setCurrentAmount(newCurrentAmount);
                goal.setUpdatedAt(LocalDateTime.now());

                FinancialGoal updatedGoal = goalRepository.save(goal);

                return mapToResponse(updatedGoal);
        }

        private FinancialGoal getOwnedGoal(
                        Long goalId,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                return goalRepository
                                .findByIdAndUserId(
                                                goalId,
                                                user.getId())
                                .orElseThrow(() -> new FinancialGoalNotFoundException(
                                                "Financial goal not found with id: "
                                                                + goalId));
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

        private void validateContributionDate(
                        LocalDate contributionDate) {

                if (contributionDate.isAfter(LocalDate.now())) {

                        throw new InvalidFinancialGoalException(
                                        "Contribution date cannot be in the future");
                }
        }

        private FinancialGoalResponse mapToResponse(
                        FinancialGoal goal) {

                BigDecimal target = goal.getTargetAmount();

                BigDecimal current = goal.getCurrentAmount();

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

                if (percentage.compareTo(
                                BigDecimal.valueOf(100)) > 0) {

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

        public GoalContributionPageResponse getContributions(
                Long goalId,
                Authentication authentication,
                LocalDate startDate,
                LocalDate endDate,
                BigDecimal minAmount,
                BigDecimal maxAmount,
                int page,
                int size,
                String sortBy,
                String direction) {

        FinancialGoal goal =
            getOwnedGoal(
                    goalId,
                    authentication
            );

        validateContributionFilters(
            startDate,
            endDate,
            minAmount,
            maxAmount
        );

        if (page < 0) {
        throw new InvalidFinancialGoalException(
                "Page number cannot be negative"
        );
        }

        if (size < 1 || size > 100) {
        throw new InvalidFinancialGoalException(
                "Page size must be between 1 and 100"
        );
        }

        String safeSortBy =
            validateContributionSortField(sortBy);

        Sort.Direction sortDirection =
            direction.equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;

        Pageable pageable =
            PageRequest.of(
                    page,
                    size,
                    Sort.by(
                            sortDirection,
                            safeSortBy
                    )
            );

        Specification<GoalContribution> specification =
            GoalContributionSpecification
                    .filterContributions(
                            goal.getId(),
                            startDate,
                            endDate,
                            minAmount,
                            maxAmount
                    );

        Page<GoalContribution> contributionPage =
            contributionRepository.findAll(
                    specification,
                    pageable
            );

        List<GoalContributionResponse> contributions =
            contributionPage
                    .getContent()
                    .stream()
                    .map(this::mapContributionToResponse)
                    .toList();

        return new GoalContributionPageResponse(
            contributions,
            contributionPage.getNumber(),
            contributionPage.getTotalPages(),
            contributionPage.getTotalElements(),
            contributionPage.getSize()
        );
        }

        private void validateContributionFilters(
                        LocalDate startDate,
                        LocalDate endDate,
                        BigDecimal minAmount,
                        BigDecimal maxAmount) {

                if (startDate != null &&
                                endDate != null &&
                                startDate.isAfter(endDate)) {

                        throw new InvalidFinancialGoalException(
                                        "Start date cannot be after end date");
                }

                if (minAmount != null &&
                                minAmount.compareTo(BigDecimal.ZERO) < 0) {

                        throw new InvalidFinancialGoalException(
                                        "Minimum amount cannot be negative");
                }

                if (maxAmount != null &&
                                maxAmount.compareTo(BigDecimal.ZERO) < 0) {

                        throw new InvalidFinancialGoalException(
                                        "Maximum amount cannot be negative");
                }

                if (minAmount != null &&
                                maxAmount != null &&
                                minAmount.compareTo(maxAmount) > 0) {

                        throw new InvalidFinancialGoalException(
                                        "Minimum amount cannot be greater than maximum amount");
                }
        }

        private String validateContributionSortField(
                        String sortBy) {

                if (sortBy == null ||
                                sortBy.trim().isEmpty()) {

                        return "contributionDate";
                }

                if (!sortBy.equals("id") &&
                                !sortBy.equals("amount") &&
                                !sortBy.equals("contributionDate") &&
                                !sortBy.equals("createdAt")) {

                        throw new InvalidFinancialGoalException(
                                        "Invalid sort field: " + sortBy);
                }

                return sortBy;
        }

        private GoalContributionResponse mapContributionToResponse(
                        GoalContribution contribution) {

                return new GoalContributionResponse(
                                contribution.getId(),
                                contribution.getAmount(),
                                contribution.getContributionDate(),
                                contribution.getNote());
        }

        public List<FinancialGoalResponse> getAllGoalsForDashboard(Long userId) {

        List<FinancialGoal> goals = goalRepository.findByUserId(userId);

        List<FinancialGoalResponse> response =
            new ArrayList<>();

        for (FinancialGoal goal : goals) {

        BigDecimal currentAmount =
                goal.getCurrentAmount();

        BigDecimal targetAmount =
                goal.getTargetAmount();

        BigDecimal remainingAmount =
                targetAmount.subtract(currentAmount);

        BigDecimal percentageCompleted =
                BigDecimal.ZERO;

        if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {

            percentageCompleted =
                    currentAmount
                            .divide(
                                    targetAmount,
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
        }

        String status;

        LocalDate today = LocalDate.now();

        if (currentAmount.compareTo(targetAmount) >= 0) {

            status = "COMPLETED";

        } else if (today.isAfter(goal.getTargetDate())) {

            status = "OVERDUE";

        } else {

            status = "IN_PROGRESS";
        }

        response.add(
                new FinancialGoalResponse(
                        goal.getId(),
                        goal.getName(),
                        targetAmount,
                        currentAmount,
                        remainingAmount,
                        percentageCompleted,
                        goal.getTargetDate(),
                        goal.getDescription(),
                        status
                )
        );
    }

    return response;
}
}
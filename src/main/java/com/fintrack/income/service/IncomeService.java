package com.fintrack.income.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.income.dto.CreateIncomeRequest;
import com.fintrack.income.dto.IncomeResponse;
import com.fintrack.income.entity.Income;
import com.fintrack.common.exception.InvalidIncomeException;
import com.fintrack.common.exception.IncomeNotFoundException;
import com.fintrack.income.repository.IncomeRepository;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public IncomeService(
            IncomeRepository incomeRepository,
            UserRepository userRepository) {

        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
    }

    public IncomeResponse createIncome(
            CreateIncomeRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        validateIncomeDate(request.getIncomeDate());

        Income income = new Income();

        income.setSource(request.getSource().trim());
        income.setAmount(request.getAmount());
        income.setDescription(request.getDescription());
        income.setIncomeDate(request.getIncomeDate());
        income.setIncomeType(request.getIncomeType().trim());
        income.setUser(user);

        LocalDateTime now = LocalDateTime.now();

        income.setCreatedAt(now);
        income.setUpdatedAt(now);

        Income savedIncome = incomeRepository.save(income);

        return mapToResponse(savedIncome);
    }

    public IncomeResponse getIncomeById(
            Long incomeId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Income income = incomeRepository
                .findByIdAndUserId(incomeId, user.getId())
                .orElseThrow(() -> new IncomeNotFoundException(
                        "Income not found with id: " + incomeId));

        return mapToResponse(income);
    }

    private User getAuthenticatedUser(Authentication authentication) {

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private void validateIncomeDate(LocalDate incomeDate) {

        if (incomeDate.isAfter(LocalDate.now())) {
            throw new InvalidIncomeException(
                "Income date cannot be in the future");
        }
    }

    private IncomeResponse mapToResponse(Income income) {

        return new IncomeResponse(
                income.getId(),
                income.getSource(),
                income.getAmount(),
                income.getDescription(),
                income.getIncomeDate(),
                income.getIncomeType());
    }
}
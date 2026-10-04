package com.fintrack.income.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.common.exception.IncomeNotFoundException;
import com.fintrack.common.exception.InvalidIncomeException;
import com.fintrack.income.dto.CreateIncomeRequest;
import com.fintrack.income.dto.IncomePageResponse;
import com.fintrack.income.dto.IncomeResponse;
import com.fintrack.income.entity.Income;
import com.fintrack.income.repository.IncomeRepository;
import com.fintrack.income.specification.IncomeSpecification;

@Service
public class IncomeService {

        private final IncomeRepository incomeRepository;
        private final UserRepository userRepository;

        private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
                        "id",
                        "source",
                        "amount",
                        "incomeDate",
                        "incomeType",
                        "createdAt",
                        "updatedAt");

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

        public IncomePageResponse filterIncomes(
                        Authentication authentication,
                        String incomeType,
                        LocalDate startDate,
                        LocalDate endDate,
                        BigDecimal minAmount,
                        BigDecimal maxAmount,
                        String search,
                        int page,
                        int size,
                        String sortBy,
                        String direction) {

                User user = getAuthenticatedUser(authentication);

                validatePagination(page, size);

                validateFilters(
                                startDate,
                                endDate,
                                minAmount,
                                maxAmount);

                String safeSortBy = validateSortField(sortBy);

                Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC;

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(
                                                sortDirection,
                                                safeSortBy));

                Specification<Income> specification = IncomeSpecification.filterIncomes(
                                user.getId(),
                                incomeType,
                                startDate,
                                endDate,
                                minAmount,
                                maxAmount,
                                search);

                Page<Income> incomePage = incomeRepository.findAll(
                                specification,
                                pageable);

                List<IncomeResponse> incomes = incomePage
                                .getContent()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();

                return new IncomePageResponse(
                                incomes,
                                incomePage.getNumber(),
                                incomePage.getTotalPages(),
                                incomePage.getTotalElements(),
                                incomePage.getSize());
        }

        public IncomeResponse getIncomeById(
                        Long incomeId,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                Income income = incomeRepository
                                .findByIdAndUserId(
                                                incomeId,
                                                user.getId())
                                .orElseThrow(() -> new IncomeNotFoundException(
                                                "Income not found with id: "
                                                                + incomeId));

                return mapToResponse(income);
        }

        public IncomeResponse updateIncome(
                        Long incomeId,
                        CreateIncomeRequest request,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                Income income = incomeRepository
                                .findByIdAndUserId(
                                                incomeId,
                                                user.getId())
                                .orElseThrow(() -> new IncomeNotFoundException(
                                                "Income not found with id: "
                                                                + incomeId));

                validateIncomeDate(
                                request.getIncomeDate());

                income.setSource(
                                request.getSource().trim());

                income.setAmount(
                                request.getAmount());

                income.setDescription(
                                request.getDescription());

                income.setIncomeDate(
                                request.getIncomeDate());

                income.setIncomeType(
                                request.getIncomeType().trim());

                income.setUpdatedAt(
                                LocalDateTime.now());

                Income updatedIncome = incomeRepository.save(income);

                return mapToResponse(updatedIncome);
        }

        public void deleteIncome(
                        Long incomeId,
                        Authentication authentication) {

                User user = getAuthenticatedUser(authentication);

                Income income = incomeRepository
                                .findByIdAndUserId(
                                                incomeId,
                                                user.getId())
                                .orElseThrow(() -> new IncomeNotFoundException(
                                                "Income not found with id: "
                                                                + incomeId));

                incomeRepository.delete(income);
        }

        private User getAuthenticatedUser(
                        Authentication authentication) {

                String email = authentication.getName();

                return userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new RuntimeException(
                                                "Authenticated user not found"));
        }

        private void validateIncomeDate(
                        LocalDate incomeDate) {

                if (incomeDate.isAfter(LocalDate.now())) {

                        throw new InvalidIncomeException(
                                        "Income date cannot be in the future");
                }
        }

        private void validatePagination(
                        int page,
                        int size) {

                if (page < 0) {

                        throw new InvalidIncomeException(
                                        "Page number cannot be negative");
                }

                if (size < 1 || size > 100) {

                        throw new InvalidIncomeException(
                                        "Page size must be between 1 and 100");
                }
        }

        private void validateFilters(
                        LocalDate startDate,
                        LocalDate endDate,
                        BigDecimal minAmount,
                        BigDecimal maxAmount) {

                if (startDate != null &&
                                endDate != null &&
                                startDate.isAfter(endDate)) {

                        throw new InvalidIncomeException(
                                        "Start date cannot be after end date");
                }

                if (minAmount != null &&
                                minAmount.compareTo(BigDecimal.ZERO) < 0) {

                        throw new InvalidIncomeException(
                                        "Minimum amount cannot be negative");
                }

                if (maxAmount != null &&
                                maxAmount.compareTo(BigDecimal.ZERO) < 0) {

                        throw new InvalidIncomeException(
                                        "Maximum amount cannot be negative");
                }

                if (minAmount != null &&
                                maxAmount != null &&
                                minAmount.compareTo(maxAmount) > 0) {

                        throw new InvalidIncomeException(
                                        "Minimum amount cannot be greater than maximum amount");
                }
        }

        private String validateSortField(
                        String sortBy) {

                if (sortBy == null ||
                                sortBy.trim().isEmpty()) {

                        return "incomeDate";
                }

                String field = sortBy.trim();

                if (!ALLOWED_SORT_FIELDS.contains(field)) {

                        throw new InvalidIncomeException(
                                        "Invalid sort field: " + field);
                }

                return field;
        }

        private IncomeResponse mapToResponse(
                        Income income) {

                return new IncomeResponse(
                                income.getId(),
                                income.getSource(),
                                income.getAmount(),
                                income.getDescription(),
                                income.getIncomeDate(),
                                income.getIncomeType());
        }
}
package com.fintrack.income.specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.fintrack.income.entity.Income;

import jakarta.persistence.criteria.Predicate;

public class IncomeSpecification {

        private IncomeSpecification() {
        }

        public static Specification<Income> filterIncomes(
                        Long userId,
                        String incomeType,
                        LocalDate startDate,
                        LocalDate endDate,
                        BigDecimal minAmount,
                        BigDecimal maxAmount,
                        String search) {

                return (root, query, criteriaBuilder) -> {

                        List<Predicate> predicates = new ArrayList<>();

                        // Only authenticated user's income
                        predicates.add(
                                        criteriaBuilder.equal(
                                                        root.get("user").get("id"),
                                                        userId));

                        // Income type filter
                        if (incomeType != null &&
                                        !incomeType.trim().isEmpty()) {

                                predicates.add(
                                                criteriaBuilder.equal(
                                                                criteriaBuilder.lower(
                                                                                root.get("incomeType")),
                                                                incomeType.trim().toLowerCase()));
                        }

                        // Start date
                        if (startDate != null) {

                                predicates.add(
                                                criteriaBuilder.greaterThanOrEqualTo(
                                                                root.get("incomeDate"),
                                                                startDate));
                        }

                        // End date
                        if (endDate != null) {

                                predicates.add(
                                                criteriaBuilder.lessThanOrEqualTo(
                                                                root.get("incomeDate"),
                                                                endDate));
                        }

                        // Minimum amount
                        if (minAmount != null) {

                                predicates.add(
                                                criteriaBuilder.greaterThanOrEqualTo(
                                                                root.get("amount"),
                                                                minAmount));
                        }

                        // Maximum amount
                        if (maxAmount != null) {

                                predicates.add(
                                                criteriaBuilder.lessThanOrEqualTo(
                                                                root.get("amount"),
                                                                maxAmount));
                        }

                        // Search source OR description
                        if (search != null &&
                                        !search.trim().isEmpty()) {

                                String keyword = "%" + search.trim().toLowerCase() + "%";

                                Predicate sourcePredicate = criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                                root.get("source")),
                                                keyword);

                                Predicate descriptionPredicate = criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                                root.get("description")),
                                                keyword);

                                predicates.add(
                                                criteriaBuilder.or(
                                                                sourcePredicate,
                                                                descriptionPredicate));
                        }

                        return criteriaBuilder.and(
                                        predicates.toArray(
                                                        new Predicate[0]));
                };
        }
}
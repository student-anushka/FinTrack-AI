package com.fintrack.budget.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.fintrack.budget.entity.Budget;

import jakarta.persistence.criteria.Predicate;

public class BudgetSpecification {

    private BudgetSpecification() {
    }

    public static Specification<Budget> filterBudgets(
            Long userId,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            String search) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Always restrict budgets to logged-in user
            predicates.add(
                    criteriaBuilder.equal(
                            root.get("user").get("id"),
                            userId));

            // Category filter
            if (categoryId != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("category").get("id"),
                                categoryId));
            }

            // Budget starts on or after this date
            if (startDate != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("startDate"),
                                startDate));
            }

            // Budget ends on or before this date
            if (endDate != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("endDate"),
                                endDate));
            }

            // Search budget name
            if (search != null
                    && !search.trim().isEmpty()) {

                String keyword = "%" +
                        search.trim().toLowerCase() +
                        "%";

                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("name")),
                                keyword));
            }

            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]));
        };
    }
}
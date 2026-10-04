package com.fintrack.expense.specification;

import com.fintrack.expense.entity.Expense;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    private ExpenseSpecification() {
    }

    public static Specification<Expense> filterExpenses(
            Long userId,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String search) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Always restrict results to logged-in user
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

            // Start date filter
            if (startDate != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("expenseDate"),
                                startDate));
            }

            // End date filter
            if (endDate != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("expenseDate"),
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

            // Search title and description
            if (search != null && !search.trim().isEmpty()) {

                String keyword = "%" + search.trim().toLowerCase() + "%";

                Predicate titleMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("title")),
                        keyword);

                Predicate descriptionMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("description")),
                        keyword);

                predicates.add(
                        criteriaBuilder.or(
                                titleMatch,
                                descriptionMatch));
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0]));
        };
    }
}
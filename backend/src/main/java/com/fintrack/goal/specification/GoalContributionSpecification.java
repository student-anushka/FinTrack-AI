package com.fintrack.goal.specification;

import com.fintrack.goal.entity.GoalContribution;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GoalContributionSpecification {

    private GoalContributionSpecification() {
    }

    public static Specification<GoalContribution> filterContributions(
            Long goalId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    criteriaBuilder.equal(
                            root.get("goal").get("id"),
                            goalId));

            if (startDate != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("contributionDate"),
                                startDate));
            }

            if (endDate != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("contributionDate"),
                                endDate));
            }

            if (minAmount != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("amount"),
                                minAmount));
            }

            if (maxAmount != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("amount"),
                                maxAmount));
            }

            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]));
        };
    }
}
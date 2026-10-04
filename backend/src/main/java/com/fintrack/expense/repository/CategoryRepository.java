package com.fintrack.expense.repository;

import com.fintrack.expense.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
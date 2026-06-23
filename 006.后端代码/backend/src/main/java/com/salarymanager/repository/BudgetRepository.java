package com.salarymanager.repository;

import com.salarymanager.model.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserIdAndBudgetMonthAndCategoryId(Long userId, String budgetMonth, Long categoryId);
    Optional<Budget> findByUserIdAndBudgetMonthAndCategoryIdIsNull(Long userId, String budgetMonth);
}

package com.salarymanager.repository;

import com.salarymanager.model.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdAndDeletedAtIsNullOrderByExpenseDateDescCreatedAtDesc(Long userId);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.expenseDate BETWEEN :start AND :end AND e.deletedAt IS NULL ORDER BY e.expenseDate DESC, e.createdAt DESC")
    List<Expense> findByUserIdAndMonth(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.categoryId = :categoryId AND e.expenseDate BETWEEN :start AND :end AND e.deletedAt IS NULL ORDER BY e.expenseDate DESC")
    List<Expense> findByUserIdAndCategoryAndMonth(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.userId = :userId AND e.expenseDate BETWEEN :start AND :end AND e.deletedAt IS NULL")
    BigDecimal sumByUserIdAndMonth(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e.categoryId as categoryId, COALESCE(SUM(e.amount), 0) as total FROM Expense e WHERE e.userId = :userId AND e.expenseDate BETWEEN :start AND :end AND e.deletedAt IS NULL GROUP BY e.categoryId ORDER BY total DESC")
    List<Object[]> categoryBreakdown(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e.expenseDate as date, COALESCE(SUM(e.amount), 0) as total FROM Expense e WHERE e.userId = :userId AND e.expenseDate BETWEEN :start AND :end AND e.deletedAt IS NULL GROUP BY e.expenseDate ORDER BY e.expenseDate")
    List<Object[]> dailyTrend(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}

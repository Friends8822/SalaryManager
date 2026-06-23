package com.salarymanager.repository;

import com.salarymanager.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL AND (c.userId = :userId OR c.userId IS NULL) ORDER BY c.sortOrder ASC")
    List<Category> findAvailableByUser(@Param("userId") Long userId);
}

package com.salarymanager.controller;

import com.salarymanager.model.dto.ApiResponse;
import com.salarymanager.model.dto.Requests.*;
import com.salarymanager.model.entity.Category;
import com.salarymanager.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final ExpenseService expenseService;

    public CategoryController(ExpenseService expenseService) { this.expenseService = expenseService; }

    @GetMapping
    public ApiResponse<List<Category>> list(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(expenseService.getCategories(userId));
    }

    @PostMapping
    public ApiResponse<Category> add(Authentication auth, @Valid @RequestBody CategoryRequest req) {
        try {
            Long userId = (Long) auth.getPrincipal();
            return ApiResponse.ok(expenseService.addCategory(userId, req));
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(Authentication auth, @PathVariable Long id) {
        try {
            Long userId = (Long) auth.getPrincipal();
            expenseService.deleteCategory(userId, id);
            return ApiResponse.ok("已删除", null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }
}

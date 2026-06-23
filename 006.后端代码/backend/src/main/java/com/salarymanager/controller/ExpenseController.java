package com.salarymanager.controller;

import com.salarymanager.model.dto.ApiResponse;
import com.salarymanager.model.dto.Requests.*;
import com.salarymanager.model.entity.Expense;
import com.salarymanager.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) { this.expenseService = expenseService; }

    @GetMapping
    public ApiResponse<List<Expense>> list(Authentication auth,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Long categoryId) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(expenseService.listExpenses(userId, month, categoryId));
    }

    @PostMapping
    public ApiResponse<Expense> add(Authentication auth, @Valid @RequestBody ExpenseRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok("已记录", expenseService.addExpense(userId, req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Expense> update(Authentication auth, @PathVariable Long id, @Valid @RequestBody ExpenseRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok("已更新", expenseService.updateExpense(userId, id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        expenseService.deleteExpense(userId, id);
        return ApiResponse.ok("已删除", null);
    }
}

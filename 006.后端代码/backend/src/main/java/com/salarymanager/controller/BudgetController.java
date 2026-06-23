package com.salarymanager.controller;

import com.salarymanager.model.dto.ApiResponse;
import com.salarymanager.model.dto.Requests.*;
import com.salarymanager.model.entity.Budget;
import com.salarymanager.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final ExpenseService expenseService;

    public BudgetController(ExpenseService expenseService) { this.expenseService = expenseService; }

    @GetMapping
    public ApiResponse<?> get(Authentication auth, @RequestParam(required = false) String month) {
        Long userId = (Long) auth.getPrincipal();
        if (month == null) month = java.time.YearMonth.now().toString();
        Budget b = expenseService.getBudget(userId, month);
        return ApiResponse.ok(b);
    }

    @PostMapping
    public ApiResponse<Budget> set(Authentication auth, @Valid @RequestBody BudgetRequest req) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok("预算已保存", expenseService.setBudget(userId, req));
    }
}

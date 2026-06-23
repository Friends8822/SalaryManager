package com.salarymanager.controller;

import com.salarymanager.model.dto.ApiResponse;
import com.salarymanager.service.ExpenseService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final ExpenseService expenseService;

    public StatsController(ExpenseService expenseService) { this.expenseService = expenseService; }

    @GetMapping("/monthly")
    public ApiResponse<Map<String, Object>> monthly(Authentication auth, @RequestParam(required = false) String month) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.ok(expenseService.getMonthlyStats(userId, month));
    }
}

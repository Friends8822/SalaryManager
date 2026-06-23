package com.salarymanager.model.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class Requests {

    public static class LoginRequest {
        @NotBlank public String username;
        @NotBlank public String password;
    }

    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 32) public String username;
        @NotBlank @Size(min = 6, max = 64) public String password;
    }

    public static class ExpenseRequest {
        @NotNull public Long categoryId;
        @NotNull @DecimalMin("0.01") @DecimalMax("99999999.99") public BigDecimal amount;
        public String note;
        @NotBlank public String expenseDate;
    }

    public static class BudgetRequest {
        @NotBlank public String budgetMonth;
        public Long categoryId;
        @NotNull @DecimalMin("0.01") public BigDecimal amount;
    }

    public static class CategoryRequest {
        @NotBlank @Size(min = 1, max = 6) public String name;
        @NotBlank public String icon;
        @NotBlank public String color;
    }

    public static class ChangePasswordRequest {
        @NotBlank public String oldPassword;
        @NotBlank @Size(min = 6, max = 64) public String newPassword;
    }
}

package com.salarymanager.controller;

import com.salarymanager.model.dto.ApiResponse;
import com.salarymanager.model.dto.Requests.*;
import com.salarymanager.model.entity.User;
import com.salarymanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            User u = userService.register(req);
            return ApiResponse.ok(Map.of("id", u.getId(), "username", u.getUsername()));
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            String token = userService.login(req);
            return ApiResponse.ok(Map.of("token", token, "username", req.username));
        } catch (RuntimeException e) {
            return ApiResponse.fail(401, e.getMessage());
        }
    }

    @GetMapping("/me")
    public ApiResponse<?> me(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userService.getById(userId);
        return ApiResponse.ok(Map.of("id", u.getId(), "username", u.getUsername(), "nickname", u.getNickname()));
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(Authentication auth, @Valid @RequestBody ChangePasswordRequest req) {
        try {
            Long userId = (Long) auth.getPrincipal();
            userService.changePassword(userId, req);
            return ApiResponse.ok("密码修改成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }
}

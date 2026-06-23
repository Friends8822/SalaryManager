package com.salarymanager.service;

import com.salarymanager.model.dto.Requests.*;
import com.salarymanager.model.entity.User;
import com.salarymanager.repository.UserRepository;
import com.salarymanager.security.JwtUtil;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) { this.userRepo = userRepo; }

    public User register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.username))
            throw new RuntimeException("用户名已存在");
        User u = new User();
        u.setUsername(req.username);
        u.setPasswordHash(sha256(req.password));
        u.setNickname(req.username);
        return userRepo.save(u);
    }

    public String login(LoginRequest req) {
        User u = userRepo.findByUsername(req.username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (!u.getPasswordHash().equals(sha256(req.password)))
            throw new RuntimeException("用户名或密码错误");
        if (u.getStatus() != 1)
            throw new RuntimeException("账号已被禁用");
        return JwtUtil.generateToken(u.getId(), u.getUsername());
    }

    public User getById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public void changePassword(Long userId, ChangePasswordRequest req) {
        User u = getById(userId);
        if (!u.getPasswordHash().equals(sha256(req.oldPassword)))
            throw new RuntimeException("原密码错误");
        u.setPasswordHash(sha256(req.newPassword));
        userRepo.save(u);
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}

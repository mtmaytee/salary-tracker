package com.dev.salarytracker.controller;

import com.dev.salarytracker.dto.MessageResponse;
import com.dev.salarytracker.dto.RegisterRequest;
import com.dev.salarytracker.dto.AuthResponse;
import com.dev.salarytracker.dto.LoginRequest;
import com.dev.salarytracker.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        AuthResponse response = authService.authenticate(request,ip);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        // ดึง IP Address ของผู้ใช้งาน
        String ip = servletRequest.getRemoteAddr();

        authService.registerUser(request,ip);
        return ResponseEntity.ok(new MessageResponse("ลงทะเบียนสำเร็จ"));
    }

}

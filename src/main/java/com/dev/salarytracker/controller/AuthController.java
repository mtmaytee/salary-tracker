package com.dev.salarytracker.controller;

import com.dev.salarytracker.dto.MessageResponse;
import com.dev.salarytracker.dto.RegisterRequest;
import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.repository.UsersRepository;
import com.dev.salarytracker.dto.AuthResponse;
import com.dev.salarytracker.dto.LoginRequest;
import com.dev.salarytracker.service.AuthService;
import com.dev.salarytracker.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsersRepository usersRepository; // ดึง Repository เข้ามา
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        AuthResponse response = authService.authenticate(request,ip);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable String token){
        // ค้นหา User จาก Token ที่ได้รับ
        Users user = usersRepository.findByVerificationToken(token);

        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Link ยืนยันไม่ถูกต้อง หรือหมดอายุ"));
        }

        // อัปเดตสถานะเป็น active (true)
        user.setActiveStatus(true);
        user.setVerificationToken(null); // ลบ Token ทิ้งหลังจากใช้แล้ว
        usersRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("ยืนยันตัวตนสำเร็จ! คุณสามารถ Login ได้แล้ว")); // [cite: 200]
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        // ดึง IP Address ของผู้ใช้งาน
        String ip = servletRequest.getRemoteAddr();

        authService.registerUser(request,ip);
        return ResponseEntity.ok(new MessageResponse("ลงทะเบียนสำเร็จ กรุณาตรวจสอบอีเมล"));
    }

}

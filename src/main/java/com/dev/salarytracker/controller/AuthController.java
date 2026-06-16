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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${app.frontend-url}")
    private String frontendUrl;

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
            // กรณี Token ผิดพลาด ให้ Redirect กลับไปหน้า Login พร้อม Parameter แจ้ง Error
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendUrl + "/login?error=invalid_token"))
                    .build();
        }

        // อัปเดตสถานะเป็น active (true)
        user.setActiveStatus(true);
        user.setVerificationToken(null); // ลบ Token ทิ้งหลังจากใช้แล้ว
        usersRepository.save(user);

        // ✅ ยืนยันสำเร็จ -> Redirect ไปหน้า Login ของ Frontend พร้อม Parameter
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendUrl + "/login?verified=true"))
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        // ดึง IP Address ของผู้ใช้งาน
        String ip = servletRequest.getRemoteAddr();

        authService.registerUser(request,ip);
        return ResponseEntity.ok(new MessageResponse("ลงทะเบียนสำเร็จ กรุณาตรวจสอบอีเมล"));
    }

}

package com.dev.salarytracker.service;

import com.dev.salarytracker.config.SecurityConfig;
import com.dev.salarytracker.dto.AuthResponse;
import com.dev.salarytracker.dto.LoginRequest;
import com.dev.salarytracker.dto.RegisterRequest;
import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.exception.CustomValidationException;
import com.dev.salarytracker.exception.UserNotFoundException;
import com.dev.salarytracker.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private LogService logService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(LoginRequest request, String ip) {
        try {
            // 1. ค้นหา User
            Users user = usersRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("ไม่พบชื่อผู้ใช้งานนี้"));
            // 2. เช็ค Password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // ❌ บันทึก Log: รหัสผ่านผิด
                logService.saveLog(request.getUsername(), "LOGIN", "FAILED", "Invalid password attempt", ip);
                throw new BadCredentialsException("รหัสผ่านไม่ถูกต้อง");
            }
            // ✅ บันทึก Log: Login สำเร็จ
            logService.saveLog(user.getUsername(), "LOGIN", "SUCCESS", "User logged in successfully", ip);

            String token = jwtService.generateToken(user.getUsername());
            return new AuthResponse(token);

        } catch (UserNotFoundException e) {
            // ❌ บันทึก Log: ไม่พบ Username ในระบบ
            logService.saveLog(request.getUsername(), "LOGIN", "FAILED", "Username not found", ip);
            throw e;
        }
    }

    public Users registerUser(RegisterRequest request,String ip) {

        try {
            // สร้าง Map สำหรับเก็บสะสมข้อผิดพลาดที่เจอ
            Map<String, String> errors = new HashMap<>();

            // 1. เริ่มตรวจสอบซ้ำทีละเงื่อนไข แต่ใช้การบันทึกข้อความลง Map แทนการ throw ทันที
            if (usersRepository.existsByUsername(request.getUsername())) {
                errors.put("username", "Username นี้ถูกใช้งานแล้ว");
            }
            if (usersRepository.existsByEmail(request.getEmail())) {
                errors.put("email", "Email นี้ถูกใช้งานแล้ว");
            }
            if (usersRepository.existsByNationalId(request.getNationalId())) {
                errors.put("nationalId", "เลขบัตรประชาชนนี้มีในระบบแล้ว");
            }
            if (usersRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                errors.put("phoneNumber", "เบอร์โทรศัพท์นี้มีในระบบแล้ว");
            }

            // 2. ถ้าใน Map มี Error สะสมอยู่ (ไม่ว่างเปล่า) ให้โยน Custom Exception ออกไปทีเดียว
            if (!errors.isEmpty()) {
                // แนะนำให้สร้าง Custom Exception ที่สามารถส่ง Map ของ errors แนบออกไปได้
                // หรือส่งก้อน errors นี้ไปจัดการต่อที่ GlobalExceptionHandler เพื่อแปลงเป็น JSON ส่งให้ Angular
                throw new CustomValidationException(errors);
            }

            // 2. Map ค่าจาก DTO ไปยัง Entity
            Users user = new Users();
            user.setFirstName(request.getFirstName()); // อย่าลืมเพิ่มฟิลด์นี้ใน Entity และ DTO
            user.setLastName(request.getLastName());
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setNationalId(request.getNationalId());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setActiveStatus(false);

            // ✅ จุดสำคัญ: ต้องเอา Password ที่รับมา ไปเข้าเครื่องย่อย (Encode) ก่อนเซฟลง DB
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(hashedPassword);
            // 1. เจน Token สุ่ม (UUID)
            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);


            // 2. บันทึกลง Database
            Users savedUser = usersRepository.save(user);

            // 3. ส่งอีเมลยืนยัน
            emailService.sendVerificationEmail(savedUser.getEmail(), token);
            // ✅ บันทึก Log เมื่อสมัครสำเร็จ
            logService.saveLog(
                    savedUser.getUsername(),
                    "REGISTER",
                    "SUCCESS",
                    "User registered successfully",
                    ip
            );
            return savedUser;
        } catch (Exception e) {
            // ❌ บันทึก Log เมื่อเกิด Error
            logService.saveLog(
                    request.getUsername(),
                    "REGISTER",
                    "FAILED",
                    "Registration failed: " + e.getMessage(),
                    ip
            );
            throw e;
        }

    }
}
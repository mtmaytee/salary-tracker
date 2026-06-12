package com.dev.salarytracker.controller;


import com.dev.salarytracker.dto.RegisterRequest;
import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.exception.UserAlreadyExistsException;
import com.dev.salarytracker.repository.UsersRepository;
import com.dev.salarytracker.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://localhost:4200") // อนุญาตให้ Angular เข้าถึงได้
public class UsersController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/all")
    public List<Users> getAllUser(){
        return usersRepository.findAll();
    }

    // 🌟 ดึงข้อมูลโปรไฟล์ของคนที่ Login อยู่
    @GetMapping("/me")
    public ResponseEntity<Users> getMyProfile() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    // 🌟 แก้ไขข้อมูลส่วนตัว (รองรับทั้ง /update และ /profile)
    @PutMapping({"/update", "/profile"})
    public ResponseEntity<Users> updateProfile(@RequestBody Users updatedData) {
        return ResponseEntity.ok(authService.updateUserProfile(updatedData));
    }

    @PostMapping("/register") // แนะนำให้ใช้ path ที่สื่อสารชัดเจน
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest){
        // เปลี่ยนจาก Users เป็น RegisterRequest
        // เพิ่ม @Valid เพื่อให้ Validation ใน DTO ทำงาน
        try {
            // ดึง IP Address ของผู้ใช้งาน
            String ip = servletRequest.getRemoteAddr();

            Users savedUser = authService.registerUser(request,ip);
            return ResponseEntity.ok(savedUser);

        } catch (UserAlreadyExistsException e) {
            // แนะนำให้ catch เจาะจง Exception ที่เราสร้างขึ้น
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllUsers(){
        try {
            usersRepository.deleteAll();
            return ResponseEntity.ok("ระบบได้ทำการลบข้อมูลผู้ใช้ทั้งหมดเรียบร้อยแล้ว");
        }catch (Exception e){
            return ResponseEntity.status(500).body("ไม่สามารถลบข้อมูลได้เนื่องจาก: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOne(@PathVariable UUID id){
        try{
            usersRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.status(404).body("ไม่สามารถลบข้อมูลได้เนื่องจาก: " + e.getMessage());

    }

    }


}

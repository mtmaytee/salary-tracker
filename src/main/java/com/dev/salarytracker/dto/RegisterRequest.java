package com.dev.salarytracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username ห้ามว่าง")
    private String username;

    @Email(message = "รูปแบบอีเมลไม่ถูกต้อง")
    @NotBlank(message = "Email ห้ามว่าง")
    private String email;

    @Size(min = 13, max = 13, message = "เลขบัตรประชาชนต้องมี 13 หลัก")
    private String nationalId;

    @NotBlank(message = "เบอร์โทรศัพท์ห้ามว่าง")
    private String phoneNumber;

    @NotBlank(message = "รหัสผ่านห้ามว่าง")
    @Size(min = 6, message = "รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร")
    private String password;

    @NotBlank(message = "ชื่อ ห้ามว่าง")
    private String firstName;

    @NotBlank(message = "นามสกุล ห้ามว่าง")
    private String lastName;
}

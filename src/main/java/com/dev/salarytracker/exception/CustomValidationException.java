package com.dev.salarytracker.exception;

import java.util.Map;

public class CustomValidationException extends RuntimeException{
    private final Map<String, String> errors;

    // สร้าง Constructor มารองรับ Map เพื่อแก้ Error ที่ฟ้องใน AuthService
    public CustomValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    // Getter สำหรับดึงข้อมูล Error ออกไปใช้ใน GlobalExceptionHandler
    public Map<String, String> getErrors() {
        return errors;
    }
}

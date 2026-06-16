package com.dev.salarytracker.exception;

import org.springframework.dao.DataAccessResourceFailureException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🌟 เพิ่มตัวดักจับปัญหา Database Connection
    @ExceptionHandler({JDBCConnectionException.class, DataAccessResourceFailureException.class})
    public ResponseEntity<Map<String, String>> handleDatabaseConnectionError(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Database Connection Error");
        response.put("message", "ไม่สามารถเชื่อมต่อฐานข้อมูลได้ในขณะนี้ (Database อาจจะปิดอยู่) กรุณาลองใหม่ภายหลัง");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE); // ส่ง 503 กลับไป
    }

    // สั่งให้จับตัว CustomValidationException ตัวใหม่ของเรา
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<Map<String, String>> handleCustomValidationException(CustomValidationException ex) {
        // ส่ง Map ของ error ทั้งหมดกลับไป พร้อมสถานะ 409 Conflict หรือ 400 Bad Request
        return new ResponseEntity<>(ex.getErrors(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<String> handleUserNotVerifiedException(UserNotVerifiedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // 🌟 ตัวสุดท้ายสำหรับดักจับ Error ที่เราไม่ได้คาดคิด (ป้องกัน 502/500 แบบไม่มีข้อมูล)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", "เกิดข้อผิดพลาดบางอย่างในระบบ: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
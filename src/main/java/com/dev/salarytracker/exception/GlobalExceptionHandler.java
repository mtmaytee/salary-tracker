package com.dev.salarytracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
}
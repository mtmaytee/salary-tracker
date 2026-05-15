package com.dev.salarytracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // สร้าง Constructor ที่รับทุก field (รวม token) ให้เอง
@NoArgsConstructor  // สร้าง Constructor เปล่าให้เอง
public class AuthResponse {
    private String token;
}
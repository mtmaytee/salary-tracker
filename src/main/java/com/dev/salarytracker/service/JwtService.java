package com.dev.salarytracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function; // อย่าลืม Import ตัวนี้

@Service
public class JwtService {

    // สร้าง Key ที่มีความยาวเหมาะสมสำหรับ HS256
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // หมดอายุใน 1 ชม.
                .signWith(key) // ใช้ key object แทน string
                .compact();
    }

    // ตรวจสอบ Username จาก Token (ปรับให้ใช้โค้ดร่วมกับ extractClaim)
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 1. เมธอดหลักสำหรับตรวจสอบความถูกต้องของ Token
     */
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        // ตรวจสอบว่าชื่อผู้ใช้ตรงกัน และ Token ยังไม่หมดอายุ
        return (extractedUsername.equals(username)) && !isTokenExpired(token);
    }

    /**
     * 2. เมธอดสำหรับเช็คว่า Token หมดอายุหรือยัง
     */
    private boolean isTokenExpired(String token) {
        // เช็คว่าเวลาหมดอายุของ Token มาถึงก่อนเวลาปัจจุบัน (new Date()) หรือไม่
        return extractExpiration(token).before(new Date());
    }

    /**
     * 3. เมธอดสำหรับดึงเวลาหมดอายุ (Expiration Date) ออกมาจาก Token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ดึงเฉพาะ Claim ที่ต้องการ (แก้ไขพารามิเตอร์ให้ถูกต้อง)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ดึง Claims ทั้งหมด (แก้ไขให้ใช้ตัวแปร key โดยตรง)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // ใช้ตัวแปร key ที่เราประกาศไว้ด้านบน
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
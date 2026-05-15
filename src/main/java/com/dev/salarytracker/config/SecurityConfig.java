package com.dev.salarytracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //บอก Spring Boot ว่าไฟล์นี้เป็นไฟล์ตั้งค่า (Configuration) นะ ให้มาอ่านที่นี่ตอนเริ่มรันระบบ
@EnableWebSecurity //คำสั่งเปิดใช้งานระบบความปลอดภัย (Security) ให้กับโปรเจกต์ ถ้าไม่มีคำสั่งนี้ Spring จะไม่ใช้กฎที่เราเขียน
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // ปิด CSRF สำหรับ REST API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // ปล่อยให้ Login/Register เข้าได้โดยไม่ต้องมี Token
                        .requestMatchers("/api/users/**").authenticated() // บังคับว่า UsersController ต้อง Login ก่อน
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 🔥 เพิ่มบรรทัดนี้: บอกให้ใช้ JwtFilter ก่อน Filter มาตรฐานของ Spring
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // ใช้เทคนิค BCrypt ซึ่งปลอดภัยและเป็นมาตรฐาน
    }
}

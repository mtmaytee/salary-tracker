package com.dev.salarytracker.config;

import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.repository.UsersRepository;
import com.dev.salarytracker.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

//ไฟล์ JwtFilter.java นี้คือ "ด่านตรวจบัตร" ที่จะคอยดักฟังทุกการเชื่อมต่อที่วิ่งเข้ามาหา Server ก่อนจะส่งต่อไปยัง Controller ต่างๆ
@Component //บอก Spring ว่าให้สร้างวัตถุ (Bean) ของ Class นี้ขึ้นมาโดยอัตโนมัติ เพื่อนำไปใช้งานในระบบ Security
public class JwtFilter extends OncePerRequestFilter {
//extends OncePerRequestFilter: เป็นการสืบทอดคุณสมบัติที่บังคับว่า "หนึ่ง Request ที่ยิงเข้ามา ให้เดินผ่าน Filter นี้แค่ รอบเดียว เท่านั้น" เพื่อป้องกันการวนลูปตรวจสอบซ้ำซ้อน
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UsersRepository usersRepository;

    //เมธอดหลัก doFilterInternal เมธอดนี้คือ "จุดตรวจ" ที่จะทำงานทุกครั้งที่มีคนยิง API มาหาเรา
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //authHeader หน้าที่: ไปดูใน Header ของ Request ที่ส่งมาว่ามีช่องที่ชื่อว่า Authorization หรือไม่
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            //หน้าที่: ตัดคำว่า "Bearer " (ที่มี 7 ตัวอักษรรวมเว้นวรรค) ออกไป เพื่อให้เหลือแต่ ตัวรหัส Token เพียวๆ
            String token = authHeader.substring(7);

            //ส่งรหัส Token ไปให้ jwtService ช่วย "แกะ" ดูหน่อยว่า เจ้าของบัตรใบนี้ชื่อว่าอะไร (Username)
            String username = jwtService.extractUsername(token);
            // ตรวจสอบและตั้งค่า Security Context ที่นี่
            // ตรวจสอบเบื้องต้น และเช็คว่าในระบบ Security ตอนนี้ยังไม่มีใคร Login ค้างอยู่
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 1. ดึงข้อมูล User จากฐานข้อมูล (ผ่าน UserDetailsService หรือ Repository)
                // ใช้ .orElseThrow เพื่อจัดการกรณีหา Username นี้ในฐานข้อมูลไม่พบ
                Users userDetails = usersRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // 2. ตรวจสอบว่า Token ยังไม่หมดอายุ และข้อมูลถูกต้อง
                if (jwtService.isTokenValid(token, userDetails.getUsername())) {

                    // สร้างวัตถุ Authentication (เปรียบเสมือนบัตรผ่านที่ประทับตราแล้ว)
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            new ArrayList<>() // ตรงนี้ใส่ Authorities/Roles ถ้ามี
                    );

                    // เก็บรายละเอียดของ Request (เช่น IP Address) ลงในบัตรผ่าน
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 3. เอาชื่อใส่ลงไปใน SecurityContext เพื่อบอก Spring ว่า "คนนี้ผ่านด่านแล้วนะ"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        //หน้าที่: สั่งให้ Request นี้ "เดินหน้าต่อไปได้" (ส่งไม้ต่อให้ Filter ถัดไป หรือส่งให้ Controller)
        filterChain.doFilter(request, response);
        //คำเตือน: ถ้าคุณลืมเขียนบรรทัดนี้ Request จะค้างอยู่ที่นี่ และคนเรียก API จะไม่ได้คำตอบอะไรกลับไปเลย (หน้าจอจะหมุนค้าง)
    }
}

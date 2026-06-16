package com.dev.salarytracker.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.backend-url}")
    private String backendUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = backendUrl + "/api/auth/verify/" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ยืนยันการสมัครสมาชิก - Salary System");
        message.setText("กรุณาคลิกลิงก์ด้านล่างเพื่อยืนยันตัวตนและเปิดใช้งานบัญชีของคุณ:\n\n" + verificationUrl);

        mailSender.send(message);
    }
}

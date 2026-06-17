package com.dev.salarytracker.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.backend-url}")
    private String backendUrl;

    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            log.info("Starting to send verification email to: {}", toEmail);
            String verificationUrl = backendUrl + "/api/auth/verify/" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("ยืนยันการสมัครสมาชิก - Salary System");
            message.setText("กรุณาคลิกลิงก์ด้านล่างเพื่อยืนยันตัวตนและเปิดใช้งานบัญชีของคุณ:\n\n" + verificationUrl);

            mailSender.send(message);
            log.info("Successfully sent verification email to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}. Error: {}", toEmail, e.getMessage());
        }
    }
}

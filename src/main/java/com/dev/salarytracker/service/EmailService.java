package com.dev.salarytracker.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = "http://localhost:8080/api/auth/verify/" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ยืนยันการสมัครสมาชิก - Salary System");
        message.setText("กรุณาคลิกลิงก์ด้านล่างเพื่อยืนยันตัวตนและเปิดใช้งานบัญชีของคุณ:\n\n" + verificationUrl);

        mailSender.send(message);
    }
}

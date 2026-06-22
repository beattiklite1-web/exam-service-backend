/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    // Spring Boot sẽ tự động tiêm bean này vào dựa trên cấu hình properties
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("kimsubinn8@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            System.out.println("=> Đã gửi mail thành công đến: " + to);
        } catch (Exception e) {
            System.err.println("=> Lỗi gửi mail: " + e.getMessage());
        }
    }
}
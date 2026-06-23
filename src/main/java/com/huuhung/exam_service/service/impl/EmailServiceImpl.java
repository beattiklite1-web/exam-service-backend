/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.huuhung.exam_service.service.impl;

import com.huuhung.exam_service.service.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;
    
    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Override
    @Async
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SendGrid sg = new SendGrid(apiKey);
            
            Email from = new Email(fromEmail);
            Email toEmail = new Email(to);
            Content emailContent = new Content("text/plain", content);
            Mail mail = new Mail(from, subject, toEmail, emailContent);
            
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() == 202) {
                System.out.println("=> Đã gửi mail thành công đến: " + to);
            } else {
                System.err.println("=> Lỗi gửi mail: " + response.getStatusCode() + " - " + response.getBody());
                System.err.println("⚠️ User vẫn được tạo, nhưng mail không gửi được!");
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi gửi mail: " + e.getMessage());
            e.printStackTrace();
            System.err.println("⚠️ User vẫn được tạo, nhưng mail không gửi được!");
        }
    }
}

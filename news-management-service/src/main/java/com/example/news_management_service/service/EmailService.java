package com.example.news_management_service.service;

import com.example.news_management_service.dto.ClaimData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmailInfo(ClaimData claimData) {
//        try {
//            SimpleMailMessage mail = new SimpleMailMessage();
//            mail.setTo(to);
//            mail.setSubject(subject);
//            mail.setText(body);
//            javaMailSender.send(mail);
//        } catch (Exception exception) {
//            throw new TransactionException(exception.getMessage());
//        }
    }
}

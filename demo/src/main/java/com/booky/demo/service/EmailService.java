package com.booky.demo.service;

import com.booky.demo.model.User;
import com.booky.demo.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String emailAddress, VerificationToken token) {
        String subject = "Please verify your registration";
        String verificationUrl = "http://localhost:8080/api/user/verify?token=" + token.getToken();
        String message = "Click the link to verify your account (this is definitely not a scam): " + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(emailAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

}

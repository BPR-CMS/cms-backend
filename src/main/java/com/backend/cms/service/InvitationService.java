package com.backend.cms.service;

import com.backend.cms.request.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InvitationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    public void sendInvitation(CreateUserRequest request) {

        // Generate a unique token
        String token = generateUniqueToken();

        // Store invitation information
        userService.createUser(request);

        // Send invitation email
        sendEmail(request.getEmail(), token);

    }

    private void sendEmail(String recipientEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Invitation to CMS System");
        message.setText("Link: /setup?token= " + token);

        javaMailSender.send(message);
    }

    private String generateUniqueToken() {

        // Valid token for 24 hours
        long expirationTimeMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token + "_" + expirationTimeMillis;
    }

}

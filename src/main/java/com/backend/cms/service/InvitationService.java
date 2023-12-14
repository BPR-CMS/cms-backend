package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.User;
import com.backend.cms.model.UserType;
import com.backend.cms.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    public void sendInvitation(CreateUserRequest request) {
        String recipientEmail = request.getEmail();
        UserType userType = request.getUserType();

        User existingUser = userRepository.findByEmail(recipientEmail);
        if (existingUser != null) {
            throw new IllegalStateException("Invitation already sent");
        } else {
            handleNewUserInvitation(request, recipientEmail, userType);
        }
    }

    private void handleNewUserInvitation(CreateUserRequest request, String recipientEmail, UserType userType) {
        String token = generateUniqueToken();

        userService.createUser(request, token, userType);

        sendEmail(recipientEmail, token);
    }

    private void sendEmail(String recipientEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Invitation to CMS System");
        message.setText("http://localhost:3000/sign-up?token=" + token);
     //   message.setText("https://candid-malasada-4886cc.netlify.app/sign-up?token=" + token);

        javaMailSender.send(message);
    }

    public String generateUniqueToken() {

        // Valid token for 24 hours
        long expirationTimeMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token + "_" + expirationTimeMillis;
    }

    public boolean isTokenExpired(String token) {
        String[] parts = token.split("_");
        if (parts.length != 2) {
            return true;
        }

        long expirationTimeMillis = Long.parseLong(parts[1]);
        return System.currentTimeMillis() > expirationTimeMillis;
    }

    public boolean resendInvitation(String userId) {
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            if (isTokenExpired(user.getToken())) {
                String newToken = generateUniqueToken();
                user.setToken(newToken);
                userRepository.save(user);
                sendEmail(user.getEmail(), newToken);
                // Invitation resent successfully
                return true;
            } else {
                // Invitation is still valid
                return false;
            }
        } else {
            throw new NotFoundException();
        }
    }

    public User findByToken(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) throw new NotFoundException();
        return user;
    }
}

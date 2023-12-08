package com.backend.cms.controller;

import com.backend.cms.dto.UserDTO;
import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.User;
import com.backend.cms.request.CreateUserRequest;
import com.backend.cms.service.AuthService;
import com.backend.cms.service.InvitationService;
import com.backend.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000", "https://candid-malasada-4886cc.netlify.app"})
@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/send")
    public ResponseEntity<String> sendInvitationEmail(
            @Valid @RequestBody CreateUserRequest request) {
        try {
            authService.checkIfUserIsAdminOrThrowException();
            invitationService.sendInvitation(request);
            return ResponseEntity.ok("Invitation sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/resend/{userId}")
    public ResponseEntity<String> resendInvitation(@PathVariable String userId) {
        try {
            authService.checkIfUserIsAdminOrThrowException();
            boolean isResent = invitationService.resendInvitation(userId);
            if (isResent) {
                return ResponseEntity.ok("Invitation resent successfully!");
            } else {
                return ResponseEntity.ok("Invitation is still valid.");
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/isTokenExpired/{userId}")
    public ResponseEntity<Boolean> checkInvitationExpired(@PathVariable String userId) {
        User user = userService.findUserFailIfNotFound(userId);
        String token = user.getToken();
        boolean isExpired = invitationService.isTokenExpired(token);
        return ResponseEntity.ok(isExpired);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        User user = invitationService.findByToken(token);

        if (user != null && !invitationService.isTokenExpired(token)) {
            UserDTO userDTO = UserDTO.fromUser(user);
            return ResponseEntity.ok().body(userDTO);
        }
        return ResponseEntity.badRequest().body("Invalid or expired token");
    }

}

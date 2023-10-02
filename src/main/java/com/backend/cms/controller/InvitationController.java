package com.backend.cms.controller;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.request.CreateUserRequest;
import com.backend.cms.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendInvitationEmail(
            @Valid @RequestBody CreateUserRequest request) {
        try {
            invitationService.sendInvitation(request);
            return ResponseEntity.ok("Invitation sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/resend/{userId}")
    public ResponseEntity<String> resendInvitation(@PathVariable String userId) {
        try {
            boolean isResent = invitationService.resendInvitation(userId);
            if (isResent) {
                return ResponseEntity.ok("Invitation resent successfully!");
            } else {
                return ResponseEntity.ok("Invitation is still valid.");
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}

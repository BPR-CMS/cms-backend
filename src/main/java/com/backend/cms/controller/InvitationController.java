package com.backend.cms.controller;

import com.backend.cms.request.CreateUserRequest;
import com.backend.cms.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


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
}

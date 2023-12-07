package com.backend.cms.controller;

import com.backend.cms.service.UtilsDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = {"http://localhost:3000", "https://candid-malasada-4886cc.netlify.app"})
@RestController
@RequestMapping("/api/v1/utils")
public class UtilsDatabaseController {

    @Autowired
    private UtilsDatabaseService utilsDatabaseService;

    @PostMapping("/resetDatabase")
    public ResponseEntity<String> resetDatabase() {
        utilsDatabaseService.resetTestDatabase();
        return ResponseEntity.ok("Test database reset");
    }
}

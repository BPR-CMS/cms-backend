package com.backend.cms.controller;

import com.backend.cms.dto.RegisterUserDTO;
import com.backend.cms.request.CreateInitAdminRequest;
import com.backend.cms.service.AdminInitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000", "https://6571091abeba5e00087dc4d1--candid-malasada-4886cc.netlify.app"})
@RestController
@RequestMapping("/api/v1/admin")
public class AdminInitController {

    @Autowired
    private AdminInitializationService adminInitializationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInitController.class);

    @RequestMapping(value = "/initialize", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserDTO create(@Valid @RequestBody CreateInitAdminRequest request) {
        LOGGER.info("Creating an admin entry with information: {}", request);
        return adminInitializationService.initializeAdmin(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkAdminInitialized() {
        boolean isAdminInitialized = adminInitializationService.isAdminInitialized();
        return ResponseEntity.ok(isAdminInitialized);
    }
}

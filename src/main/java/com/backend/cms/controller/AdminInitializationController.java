package com.backend.cms.controller;

import com.backend.cms.dto.UserDTO;
import com.backend.cms.request.CreateInitAdminRequest;
import com.backend.cms.service.AdminInitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/initialize")
public class AdminInitializationController {

    @Autowired
    private AdminInitializationService adminInitializationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInitializationController.class);

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody CreateInitAdminRequest request) {
        LOGGER.info("Creating an admin entry with information: {}", request);
        return adminInitializationService.initializeAdmin(request);
    }

}

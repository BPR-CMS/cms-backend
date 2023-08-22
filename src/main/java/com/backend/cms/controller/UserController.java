package com.backend.cms.controller;

import com.backend.cms.dto.UserDTO;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.UpdateUserRequest;
import com.backend.cms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UserDTO findById(@PathVariable("id") String id) {
        LOGGER.info("Finding admin entry with id: {}", id);
        User user = userService.findUserFailIfNotFound(id);
        return UserDTO.fromUser(user);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    UserDTO update(@PathVariable("id") String id, @RequestBody UpdateUserRequest request) {
        LOGGER.info("Updating user entry with information: {}", request);
        User user = userService.findUserFailIfNotFound(id);

        // Validate updated user fields
        userService.validateUserInput(request);

        request.updateUser(user);
        user.setPassword(userService.encryptPassword(request.getPassword())); // Encrypt the password
        userService.save(user);
        LOGGER.info("Updated user entry with information: {}", user);
        return UserDTO.fromUser(user);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    UserDTO delete(@PathVariable("id") String id) {
        LOGGER.info("Deleting user entry with id: {}", id);
        User user = userService.findUserFailIfNotFound(id);
        userRepository.delete(user);
        LOGGER.info("Deleted user entry with information: {}", user);
        return UserDTO.fromUser(user);
    }
}
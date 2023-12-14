package com.backend.cms.controller;

import com.backend.cms.dto.UserDTO;
import com.backend.cms.exceptions.ConflictException;
import com.backend.cms.model.AccountStatus;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.LoginRequest;
import com.backend.cms.request.SetPasswordRequest;
import com.backend.cms.request.UpdateUserRequest;
import com.backend.cms.service.AuthService;
import com.backend.cms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:3000", "https://webease-frontend.vercel.app"})
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UserDTO findById(@PathVariable("id") String id) {
        LOGGER.info("Finding admin entry with id: {}", id);
        User user = userService.findUserFailIfNotFound(id);
        return UserDTO.fromUser(user);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PATCH)
    public UserDTO update(@PathVariable("id") String id, @Valid @RequestBody UpdateUserRequest request) {
        LOGGER.info("Updating user entry with information: {}", request);
        authService.checkIfUserIsAdminOrThrowException();
        User user = userService.findUserFailIfNotFound(id);
        userService.updateUser(user, request);
        LOGGER.info("Updated user entry with information: {}", user);
        return UserDTO.fromUser(user);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public UserDTO delete(@PathVariable("id") String id) {
        LOGGER.info("Deleting user entry with id: {}", id);
        authService.checkIfUserIsAdminOrThrowException();
        User user = userService.findUserFailIfNotFound(id);
        userRepository.delete(user);
        LOGGER.info("Deleted user entry with information: {}", user);
        return UserDTO.fromUser(user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("Processing login request: {}", loginRequest);
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserDTO> findAllUsers() {
        LOGGER.info("Finding all user entries");
        List<User> users = userService.findAllUsers();
        return users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/setPassword/{id}", method = RequestMethod.PATCH)
    public UserDTO setPassword(@PathVariable("id") String id, @Valid @RequestBody SetPasswordRequest request) {
        User user = userService.findUserFailIfNotFound(id);

        // Check if the account status is already created
        if (user.getAccountStatus() == AccountStatus.CREATED) {
            throw new ConflictException("Password already set");
        }
        userService.setUserPassword(user, request);
        return UserDTO.fromUser(user);
    }

}

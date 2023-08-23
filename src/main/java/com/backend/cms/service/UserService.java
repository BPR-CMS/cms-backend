package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.UpdateUserRequest;
import com.backend.cms.utils.FieldCleaner;
import com.backend.cms.utils.Generator;
import com.backend.cms.utils.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findUserFailIfNotFound(String id) {
        User user = userRepository.findByUserId(id);
        if (user == null) throw new NotFoundException();

        return user;
    }

    public String findNewId() {
        String id;
        do {
            id = Generator.generateId("u");
        } while (userRepository.findByUserId(id) != null);
        return id;
    }

    public void save(User user) {
        if (user != null) {
            userRepository.save(user);
        }
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
    public void updateUser(User user, UpdateUserRequest request) {
        user.setFirstName(FieldCleaner.cleanField(request.getFirstName()));
        user.setLastName(FieldCleaner.cleanField(request.getLastName()));
        user.setEmail(FieldCleaner.cleanField(request.getEmail()));

        String password = request.getPassword();
        user.setPassword(encryptPassword(FieldCleaner.cleanField(password)));

        // Save the updated user
        save(user);
    }

    public void validateUserInput(UpdateUserRequest request) {
        InputValidator.validateName(request.getFirstName());
        InputValidator.validateName(request.getLastName());
        InputValidator.validateEmail(request.getEmail());
        InputValidator.validatePassword(request.getPassword());
    }
}

package com.backend.cms.service;

import com.backend.cms.dto.RegisterUserDTO;
import com.backend.cms.dto.UserDTO;
import com.backend.cms.model.Config;
import com.backend.cms.model.User;
import com.backend.cms.model.UserType;
import com.backend.cms.repository.ConfigRepository;
import com.backend.cms.request.CreateInitAdminRequest;
import com.backend.cms.utils.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminInitializationService {

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigRepository configRepository;

    public RegisterUserDTO initializeAdmin(CreateInitAdminRequest request) {
        validateUserInput(request);

        User user = createUserFromRequest(request);

        if (isAdminInitialized()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin already initialized");
        }

        createInitialAdmin(user);
        initializeAdminConfig();

        return RegisterUserDTO.fromUser(user);
    }

    private void validateUserInput(CreateInitAdminRequest request) {
        InputValidator.validateName(request.getFirstName());
        InputValidator.validateName(request.getLastName());
        InputValidator.validateEmail(request.getEmail());
        InputValidator.validatePassword(request.getPassword());
    }

    private User createUserFromRequest(CreateInitAdminRequest request) {
        User user = request.toAdmin();
        user.setUserId(userService.findNewId());
        return user;
    }

    public boolean isAdminInitialized() {
        Config config = configRepository.findFirstBy();
        return config != null && config.isInitialized();
    }

    private void initializeAdminConfig() {
        Config config = configRepository.findFirstBy();
        if (config == null) {
            // Initializing config if it does not exist
            configRepository.save(new Config(true));
        } else {
            config.setInitialized(true);
            configRepository.save(config);
        }
    }

    public void createInitialAdmin(User user) {
        // Trim and remove extra spaces from the fields
        String trimmedFirstName = user.getFirstName().trim().replaceAll("\\s+", " ");
        String trimmedLastName = user.getLastName().trim().replaceAll("\\s+", " ");
        String trimmedEmail = user.getEmail().trim();
        String trimmedPassword = user.getPassword().trim();

        // Encrypt the password
        String encryptedPassword = userService.encryptPassword(trimmedPassword);

        // Set the trimmed and cleaned values
        user.setFirstName(trimmedFirstName);
        user.setLastName(trimmedLastName);
        user.setEmail(trimmedEmail);
        user.setPassword(encryptedPassword);
        user.setUserType(UserType.ADMIN);
        userService.save(user);
    }

}

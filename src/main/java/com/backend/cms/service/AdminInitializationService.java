package com.backend.cms.service;

import com.backend.cms.dto.UserDTO;
import com.backend.cms.model.Config;
import com.backend.cms.model.User;
import com.backend.cms.repository.ConfigRepository;
import com.backend.cms.request.CreateInitAdminRequest;
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

    public UserDTO initializeAdmin(CreateInitAdminRequest request) {
        User user = createUserFromRequest(request);

        if (isAdminInitialized()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin already initialized");
        }

        userService.createSuperAdmin(user);
        initializeAdminConfig();

        return UserDTO.fromUser(user);
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

}

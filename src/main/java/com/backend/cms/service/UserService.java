package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.AccountStatus;
import com.backend.cms.model.User;
import com.backend.cms.model.UserType;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.request.CreateInitAdminRequest;
import com.backend.cms.request.CreateUserRequest;
import com.backend.cms.request.UpdateUserRequest;
import com.backend.cms.utils.FieldCleaner;
import com.backend.cms.utils.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(CreateUserRequest request) {
        User user = FieldCleaner.cleanNewUserFields(request.toUser());
        user.setUserId(findNewId());
        user.setUserType(UserType.DEFAULT);

        boolean isPasswordSet = isPasswordSet(user);

        // Set account status based on if the password has been set
        user.setAccountStatus(AccountStatus.valueOf(isPasswordSet ? AccountStatus.CREATED.name() : AccountStatus.PENDING.name()));
        save(user);
        return user;
    }

    public boolean isPasswordSet(User user) {
        return user.getPassword() != null && !user.getPassword().isEmpty();
    }
}

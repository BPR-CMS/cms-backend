package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.utils.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
}

package com.backend.cms.service;

import com.backend.cms.exceptions.UnauthorizedException;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String login(String email, String password) {
        // Find a user with the given email
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UnauthorizedException();
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException();
        }

        // Generate a JWT token
        return jwtTokenUtil.generateToken(user.getUserId());
    }
}

package com.backend.cms.service;

import com.backend.cms.exceptions.NotFoundException;
import com.backend.cms.exceptions.UnauthorizedException;
import com.backend.cms.model.User;
import com.backend.cms.repository.UserRepository;
import com.backend.cms.security.jwt.JwtTokenUtil;
import com.backend.cms.utils.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        // Validate email format
        if (!InputValidator.isValidEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        // Find a user with the given email
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new NotFoundException();
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException();
        }
//         Generate a JWT token
        return jwtTokenUtil.generateToken(user.getUserId());
    }
}

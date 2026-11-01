package com.groupbuyorganizer.service;

import com.groupbuyorganizer.model.User;
import com.groupbuyorganizer.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String username, String email, String rawPassword) {
        String cleanUsername = username == null ? "" : username.trim().toLowerCase();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();
        if (userRepository.findByUsername(cleanUsername).isPresent()) {
            throw new IllegalArgumentException("That username is already in use.");
        }
        if (userRepository.findByEmail(cleanEmail).isPresent()) {
            throw new IllegalArgumentException("That email address is already registered.");
        }
        if (rawPassword == null || rawPassword.length() < 8 || rawPassword.length() > 72) {
            throw new IllegalArgumentException("Password must be between 8 and 72 characters.");
        }
        try {
            User user = new User(cleanUsername, cleanEmail,
                    passwordEncoder.encode(rawPassword), User.Role.USER);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("That username or email address is already registered.");
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

package com.bonheur.portfolio.services.api;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bonheur.portfolio.dto.requests.LoginRequestDto;
import com.bonheur.portfolio.dto.requests.RegisterRequestDto;
import com.bonheur.portfolio.models.User;
import com.bonheur.portfolio.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class AuthServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtServices jwtServices;

    public AuthServices(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Map<String, Object> registerUser(RegisterRequestDto dto) {
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            return Map.of("message", "User already exists", "action", 0);
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setNames(dto.getNames());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setLevel("admin");

        userRepository.save(user);

        return Map.of("message", "User registered successfully", "action", 1);
    }

    @Transactional
    public Map<String, Object> loginUser(LoginRequestDto dto) {
        Optional<User> userExists = userRepository.findByEmail(dto.getEmail());
        if (userExists.isEmpty()) {
            return Map.of("message", "User not found", "action", 0);
        }
        User user = userExists.get();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Map.of("message", "Invalid credentials", "action", 0);
        }

        String token = jwtServices.generateToken(user);

        return Map.of("message", "Login successful", "action", 1,
                "data", Map.of("token", token));
    }
}

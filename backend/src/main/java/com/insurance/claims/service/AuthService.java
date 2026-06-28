package com.insurance.claims.service;

import com.insurance.claims.config.JwtTokenProvider;
import com.insurance.claims.dto.JwtResponse;
import com.insurance.claims.dto.LoginRequest;
import com.insurance.claims.dto.RegisterRequest;
import com.insurance.claims.exception.ClaimException;
import com.insurance.claims.model.User;
import com.insurance.claims.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider tokenProvider;

    public JwtResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new ClaimException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ClaimException("Invalid email or password");
        }
        if (!user.getIsActive()) {
            throw new ClaimException("Account is deactivated");
        }
        String token = tokenProvider.generateToken(user.getEmail());
        return new JwtResponse(token, user.getUserId(), user.getEmail(),
            user.getRole().name(), user.getFullName());
    }

    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ClaimException("Email already registered");
        }
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(User.Role.POLICYHOLDER);
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ClaimException("User not found"));
    }
}

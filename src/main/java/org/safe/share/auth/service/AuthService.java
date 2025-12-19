package org.safe.share.auth.service;

import org.safe.share.auth.repository.UserRepository;
import org.safe.share.dto.LoginRequest;
import org.safe.share.dto.RegisterRequest;
import org.safe.share.model.User;
import org.safe.share.security.JwtUtil;
import org.safe.share.security.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(req.email);
        user.setPasswordHash(PasswordUtil.hash(req.password));

        userRepository.save(user);
    }

    public String login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!PasswordUtil.matches(req.password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return JwtUtil.generateToken(user.getId());
    }
}

package org.safe.share.auth.controller;


import jakarta.validation.Valid;
import org.safe.share.auth.service.AuthService;
import org.safe.share.common.security.SecurityUtils;
import org.safe.share.dto.LoginRequest;
import org.safe.share.dto.RegisterRequest;
import org.safe.share.dto.TokenResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        String token = authService.login(req);
        return new TokenResponse(token);
    }

    @GetMapping("/me")
    public Long me() {
        return SecurityUtils.getCurrentUserId();
    }

}

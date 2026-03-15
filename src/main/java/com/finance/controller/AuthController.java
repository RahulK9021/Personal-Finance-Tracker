package com.finance.controller;

import com.finance.dto.LoginRequest;
import com.finance.dto.LoginResponse;
import com.finance.dto.RegisterRequest;
import com.finance.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest){
        authService.register(registerRequest);
        return "User registered successfully ";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        String token = authService.login(request);
        return new LoginResponse(token);
    }
}

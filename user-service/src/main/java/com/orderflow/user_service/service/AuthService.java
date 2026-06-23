package com.orderflow.user_service.service;

import com.orderflow.user_service.dto.request.LoginRequest;
import com.orderflow.user_service.dto.response.AuthResponse;
import com.orderflow.user_service.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail().trim().toLowerCase(), loginRequest.getPassword()));
        AuthResponse authResponse = new AuthResponse();
        authResponse.setEmail(authenticate.getName());
        String role = authenticate.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
        authResponse.setRole(role);
        String token = jwtUtil.generateToken(authenticate.getName(), role);
        authResponse.setToken(token);
        return authResponse;
    }
}

package com.example.server.service;

import com.example.server.dto.auth.JwtResponse;
import com.example.server.dto.auth.LoginRequest;
import com.example.server.dto.auth.RegisterRequest;

import com.example.server.dto.auth.TokenRefreshRequest;
import com.example.server.dto.auth.TokenRefreshResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    JwtResponse register(RegisterRequest request);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
}

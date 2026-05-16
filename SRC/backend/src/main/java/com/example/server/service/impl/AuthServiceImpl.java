package com.example.server.service.impl;

import com.example.server.dto.auth.JwtResponse;
import com.example.server.dto.auth.LoginRequest;
import com.example.server.dto.auth.RegisterRequest;
import com.example.server.dto.auth.TokenRefreshRequest;
import com.example.server.dto.auth.TokenRefreshResponse;
import com.example.server.entity.RefreshToken;
import com.example.server.entity.User;
import com.example.server.enums.Role;
import com.example.server.exception.AppException;
import com.example.server.repository.RefreshTokenRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.JwtUtils;
import com.example.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}") // Default 7 days
    private Long refreshTokenDurationMs;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_DURATION_MINUTES = 15;

    @Override
    @Transactional
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Invalid email or password", "AUTH_FAILED"));

        if (user.getAccountLockedUntil() != null) {
            if (user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
                throw new AppException(HttpStatus.UNAUTHORIZED, "Account is locked due to too many failed attempts. Try again later.", "ACCOUNT_LOCKED");
            } else {
                // Lock has expired, reset it
                user.setAccountLockedUntil(null);
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails);
            
            // Reset attempts on successful login
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            userRepository.save(user);

            String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            
            RefreshToken refreshToken = createRefreshToken(user.getId());

            return JwtResponse.builder()
                    .accessToken(jwt)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .email(userDetails.getEmail())
                    .fullName(userDetails.getFullName())
                    .role(role)
                    .userId(userDetails.getId())
                    .build();
        } catch (Exception e) {
            // Handle failed login
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION_MINUTES));
                userRepository.save(user);
                throw new AppException(HttpStatus.UNAUTHORIZED, "Account is locked due to too many failed attempts. Try again later.", "ACCOUNT_LOCKED");
            }
            
            userRepository.save(user);
            throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid email or password", "AUTH_FAILED");
        }
    }

    @Override
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Email is already in use", "EMAIL_EXISTS");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setRole(Role.ROLE_CUSTOMER); // Default role
        user.setActive(true);
        user.setDeleted(false);
        user.setFailedLoginAttempts(0);

        User savedUser = userRepository.save(user);

        String jwt = jwtUtils.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole(), savedUser.getFullName());
        RefreshToken refreshToken = createRefreshToken(savedUser.getId());

        return JwtResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .userId(savedUser.getId())
                .build();
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateToken(user.getId(), user.getEmail(), user.getRole(), user.getFullName());
                    return TokenRefreshResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken)
                            .build();
                })
                .orElseThrow(() -> new AppException(HttpStatus.FORBIDDEN, "Refresh token is invalid or expired", "INVALID_REFRESH_TOKEN"));
    }

    private RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found", "USER_NOT_FOUND"));

        // Delete existing token if any
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plus(java.time.Duration.ofMillis(refreshTokenDurationMs)));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(LocalDateTime.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new AppException(HttpStatus.FORBIDDEN, "Refresh token was expired. Please make a new signin request", "EXPIRED_REFRESH_TOKEN");
        }
        return token;
    }
}

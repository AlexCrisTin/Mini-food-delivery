package com.example.server.service.impl;

import com.example.server.dto.auth.JwtResponse;
import com.example.server.dto.auth.LoginRequest;
import com.example.server.dto.auth.RegisterRequest;
import com.example.server.dto.auth.TokenRefreshRequest;
import com.example.server.dto.auth.TokenRefreshResponse;
import com.example.server.entity.RefreshToken;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.repository.RefreshTokenRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private final String email = "test@example.com";
    private final String password = "password123";

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(authService, "refreshTokenDurationMs", 604800000L);
        user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword("hashedPassword");
        user.setFullName("Test User");
        user.setRole("CUSTOMER");
        user.setFailedLoginAttempts(0);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(email, password, "Test User", "12345678", null);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");
        
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        RefreshToken rt = new RefreshToken();
        rt.setToken("mockRefreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(rt);

        when(jwtUtils.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("mockJwt");

        JwtResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("Bearer", response.getTokenType());
        assertEquals("mockJwt", response.getAccessToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegistrationWithExistingEmail() {
        RegisterRequest request = new RegisterRequest(email, password, "Test User", "12345678", null);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        
        AppException ex = assertThrows(AppException.class, () -> authService.register(request));
        assertEquals("EMAIL_EXISTS", ex.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginUserSuccessfully() {
        LoginRequest request = new LoginRequest(email, password);
        Authentication authentication = mock(Authentication.class);
        
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(1L)
                .email(email)
                .fullName("Test User")
                .authorities(java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        RefreshToken rt = new RefreshToken();
        rt.setToken("mockRefreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(rt);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateToken(any(CustomUserDetails.class))).thenReturn("mockJwt");

        JwtResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockJwt", response.getAccessToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());
        assertEquals(email, response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getAccountLockedUntil());
    }

    @Test
    void shouldThrowExceptionWhenLoginWithInvalidCredentials() {
        LoginRequest request = new LoginRequest(email, "wrongPassword");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        AppException ex = assertThrows(AppException.class, () -> authService.login(request));
        assertEquals("AUTH_FAILED", ex.getErrorCode());
        assertEquals(1, user.getFailedLoginAttempts());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldLockAccountAfterMaxFailedAttempts() {
        LoginRequest request = new LoginRequest(email, "wrongPassword");
        user.setFailedLoginAttempts(4);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        AppException ex = assertThrows(AppException.class, () -> authService.login(request));
        assertEquals("ACCOUNT_LOCKED", ex.getErrorCode());
        assertEquals(5, user.getFailedLoginAttempts());
        assertNotNull(user.getAccountLockedUntil());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionWhenLoginDuringLockoutPeriod() {
        LoginRequest request = new LoginRequest(email, password);
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        
        AppException ex = assertThrows(AppException.class, () -> authService.login(request));
        assertEquals("ACCOUNT_LOCKED", ex.getErrorCode());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void shouldLoginSuccessfullyAfterLockoutExpires() {
        LoginRequest request = new LoginRequest(email, password);
        user.setAccountLockedUntil(LocalDateTime.now().minusMinutes(5));
        user.setFailedLoginAttempts(5);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(1L).email(email).fullName("Test User")
                .authorities(java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        RefreshToken rt = new RefreshToken();
        rt.setToken("mockRefreshToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(rt);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateToken(any(CustomUserDetails.class))).thenReturn("mockJwt");

        JwtResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getAccountLockedUntil());
        verify(userRepository, times(2)).save(user);
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken("validRefreshToken");
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("validRefreshToken");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        
        when(refreshTokenRepository.findByToken("validRefreshToken")).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.generateToken(anyLong(), anyString(), anyString(), anyString())).thenReturn("newAccessToken");
        
        TokenRefreshResponse response = authService.refreshToken(request);
        
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("validRefreshToken", response.getRefreshToken());
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenExpired() {
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken("expiredRefreshToken");
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("expiredRefreshToken");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        
        when(refreshTokenRepository.findByToken("expiredRefreshToken")).thenReturn(Optional.of(refreshToken));
        
        AppException ex = assertThrows(AppException.class, () -> authService.refreshToken(request));
        assertEquals("EXPIRED_REFRESH_TOKEN", ex.getErrorCode());
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }
}

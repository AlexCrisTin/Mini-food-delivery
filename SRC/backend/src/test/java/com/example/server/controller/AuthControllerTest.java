package com.example.server.controller;

import com.example.server.config.SecurityConfig;
import com.example.server.dto.auth.JwtResponse;
import com.example.server.dto.auth.LoginRequest;
import com.example.server.dto.auth.RegisterRequest;
import com.example.server.dto.auth.TokenRefreshRequest;
import com.example.server.dto.auth.TokenRefreshResponse;
import com.example.server.security.CustomUserDetailsService;
import com.example.server.security.JwtAuthFilter;
import com.example.server.security.JwtUtils;
import com.example.server.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // Stub the JwtAuthFilter mock to continue the filter chain
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        JwtResponse response = JwtResponse.builder()
                .accessToken("token")
                .refreshToken("refreshToken")
                .userId(1L)
                .email("test@test.com")
                .fullName("Name")
                .role("ROLE_CUSTOMER")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("test@test.com", "password", "Name", "123456789", null);
        JwtResponse response = JwtResponse.builder()
                .accessToken("token")
                .refreshToken("refreshToken")
                .userId(1L)
                .email("test@test.com")
                .fullName("Name")
                .role("ROLE_CUSTOMER")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken("oldRefreshToken");
        TokenRefreshResponse response = TokenRefreshResponse.builder()
                .accessToken("newToken")
                .refreshToken("oldRefreshToken")
                .build();

        when(authService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newToken"));
    }

    @Test
    void shouldFailLoginWhenValidationFails() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email", ""); // Invalid email, blank password

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

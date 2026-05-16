package com.example.server.controller;

import com.example.server.config.SecurityConfig;
import com.example.server.dto.notification.MarkAllNotificationsReadRequest;
import com.example.server.dto.notification.MarkNotificationReadRequest;
import com.example.server.dto.user.AddressRequest;
import com.example.server.dto.user.AddressResponse;
import com.example.server.dto.user.UserProfileResponse;
import com.example.server.dto.user.UserProfileUpdateRequest;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.CustomUserDetailsService;
import com.example.server.security.JwtAuthFilter;
import com.example.server.security.JwtUtils;
import com.example.server.service.NotificationService;
import com.example.server.service.UserService;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        userDetails = CustomUserDetails.builder()
                .id(1L)
                .email("user@test.com")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .build();

        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    void shouldGetMyProfileSuccessfully() throws Exception {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(1L);

        when(userService.getUserProfile(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/me")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldUpdateMyProfileSuccessfully() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setFullName("New Name");
        request.setPhone("1234567890");

        UserProfileResponse response = new UserProfileResponse();
        response.setId(1L);

        when(userService.updateUserProfile(eq(1L), any(UserProfileUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/me")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteMyProfileSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/users/me")
                .with(user(userDetails))
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetMyAddressesSuccessfully() throws Exception {
        when(userService.getUserAddresses(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/me/addresses")
                .with(user(userDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddAddressSuccessfully() throws Exception {
        AddressRequest request = new AddressRequest();
        request.setAddressLine("123 St");
        request.setLatitude(new java.math.BigDecimal("10.0"));
        request.setLongitude(new java.math.BigDecimal("10.0"));

        AddressResponse response = new AddressResponse();
        response.setId(10L);

        when(userService.addAddress(eq(1L), any(AddressRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/me/addresses")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void shouldUpdateAddressSuccessfully() throws Exception {
        AddressRequest request = new AddressRequest();
        request.setAddressLine("123 St");
        request.setLatitude(new java.math.BigDecimal("10.0"));
        request.setLongitude(new java.math.BigDecimal("10.0"));

        when(userService.updateAddress(eq(1L), eq(10L), any(AddressRequest.class))).thenReturn(new AddressResponse());

        mockMvc.perform(put("/api/users/me/addresses/10")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteAddressSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/users/me/addresses/10")
                .with(user(userDetails))
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldSetDefaultAddressSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/users/me/addresses/10/default")
                .with(user(userDetails))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetMyNotificationsSuccessfully() throws Exception {
        when(notificationService.getUserNotifications(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/me/notifications")
                .with(user(userDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldMarkNotificationReadSuccessfully() throws Exception {
        MarkNotificationReadRequest request = new MarkNotificationReadRequest();
        request.setNotificationId(100L);

        mockMvc.perform(patch("/api/users/me/notifications/read")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldMarkAllNotificationsReadSuccessfully() throws Exception {
        MarkAllNotificationsReadRequest request = new MarkAllNotificationsReadRequest();

        mockMvc.perform(patch("/api/users/me/notifications/read-all")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

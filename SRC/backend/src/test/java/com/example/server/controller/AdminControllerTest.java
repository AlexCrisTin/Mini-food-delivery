package com.example.server.controller;

import com.example.server.config.SecurityConfig;
import com.example.server.dto.user.UserRoleUpdateRequest;
import com.example.server.dto.user.UserStatusUpdateRequest;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.CustomUserDetailsService;
import com.example.server.security.JwtAuthFilter;
import com.example.server.security.JwtUtils;
import com.example.server.service.AdminService;
import com.example.server.service.ReportService;
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

import org.springframework.http.HttpHeaders;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails adminDetails;

    @BeforeEach
    void setUp() throws Exception {
        adminDetails = CustomUserDetails.builder()
                .id(1L)
                .email("admin@test.com")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

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
    void shouldGetSystemStats() throws Exception {
        when(adminService.getSystemStats()).thenReturn(new com.example.server.dto.report.AdminStatsResponse());

        mockMvc.perform(get("/api/admin/stats")
                .with(user(adminDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        when(adminService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/users")
                .with(user(adminDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetPendingRestaurants() throws Exception {
        when(adminService.getPendingRestaurants()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/restaurants/pending")
                .with(user(adminDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldApproveRestaurant() throws Exception {
        com.example.server.dto.restaurant.RestaurantApprovalRequest request = 
            new com.example.server.dto.restaurant.RestaurantApprovalRequest(true, "OK");

        mockMvc.perform(post("/api/admin/restaurants/1/approve")
                .with(user(adminDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService).approveRestaurant(eq(1L), any());
    }

    @Test
    void shouldUpdateUserRoleSuccessfully() throws Exception {
        UserRoleUpdateRequest request = new UserRoleUpdateRequest();
        request.setRole("OWNER");

        mockMvc.perform(patch("/api/admin/users/2/role")
                .with(user(adminDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService).updateUserRole(eq(2L), any());
    }

    @Test
    void shouldUpdateUserStatusSuccessfully() throws Exception {
        UserStatusUpdateRequest request = new UserStatusUpdateRequest();
        request.setActive(false);

        mockMvc.perform(patch("/api/admin/users/2/status")
                .with(user(adminDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(adminService).updateUserStatus(eq(2L), any());
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        Long targetUserId = 2L;

        mockMvc.perform(delete("/api/admin/users/{id}", targetUserId)
                .with(user(adminDetails))
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(adminService).deleteUser(targetUserId);
    }

    @Test
    void shouldReturnBadRequestWhenAdminDeletesSelf() throws Exception {
        Long targetUserId = 1L; // Same as adminDetails.id

        mockMvc.perform(delete("/api/admin/users/{id}", targetUserId)
                .with(user(adminDetails))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(adminService, never()).deleteUser(any());
    }

    @Test
    void shouldReturnBadRequestWhenAdminDemotesSelf() throws Exception {
        Long targetUserId = 1L;
        UserRoleUpdateRequest request = new UserRoleUpdateRequest();
        request.setRole("USER");

        mockMvc.perform(patch("/api/admin/users/{id}/role", targetUserId)
                .with(user(adminDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(adminService, never()).updateUserRole(any(), any());
    }

    @Test
    void shouldReturnBadRequestWhenAdminDeactivatesSelf() throws Exception {
        Long targetUserId = 1L;
        UserStatusUpdateRequest request = new UserStatusUpdateRequest();
        request.setActive(false);

        mockMvc.perform(patch("/api/admin/users/{id}/status", targetUserId)
                .with(user(adminDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(adminService, never()).updateUserStatus(any(), any());
    }

    @Test
    void shouldGetAdminReport() throws Exception {
        mockMvc.perform(get("/api/admin/reports/summary")
                .param("startDate", "2026-01-01")
                .param("endDate", "2026-05-17")
                .with(user(adminDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetRestaurantRevenue() throws Exception {
        mockMvc.perform(get("/api/admin/reports/restaurants")
                .param("startDate", "2026-01-01")
                .param("endDate", "2026-05-17")
                .with(user(adminDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldExportRevenueCsv() throws Exception {
        when(reportService.generateRevenueCsv(any(), any())).thenReturn("test,csv".getBytes());

        mockMvc.perform(get("/api/admin/reports/export/csv")
                .param("startDate", "2026-01-01")
                .param("endDate", "2026-05-17")
                .with(user(adminDetails)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment")))
                .andExpect(content().contentType("text/csv"));
    }
}

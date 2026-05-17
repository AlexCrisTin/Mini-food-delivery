package com.example.server.controller;

import com.example.server.config.SecurityConfig;
import com.example.server.dto.owner.OwnerRequestApproval;
import com.example.server.dto.owner.OwnerRequestResponse;
import com.example.server.dto.owner.OwnerRequestSubmission;
import com.example.server.enums.OwnerRequestStatus;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.CustomUserDetailsService;
import com.example.server.security.JwtAuthFilter;
import com.example.server.security.JwtUtils;
import com.example.server.service.OwnerRequestService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerRequestController.class)
@Import(SecurityConfig.class)
class OwnerRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerRequestService ownerRequestService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails userDetails;
    private CustomUserDetails adminDetails;

    @BeforeEach
    void setUp() throws Exception {
        userDetails = CustomUserDetails.builder()
                .id(1L)
                .email("user@test.com")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        adminDetails = CustomUserDetails.builder()
                .id(2L)
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
    void shouldSubmitRequestSuccessfully() throws Exception {
        OwnerRequestSubmission submission = new OwnerRequestSubmission();
        submission.setRestaurantName("Tasty Burgers");
        submission.setRestaurantAddress("123 Food St");
        submission.setRestaurantPhone("0123456789");

        OwnerRequestResponse response = OwnerRequestResponse.builder()
                .id(1L)
                .userId(1L)
                .userEmail("user@test.com")
                .restaurantName("Tasty Burgers")
                .status(OwnerRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(ownerRequestService.submitRequest(eq(1L), any(OwnerRequestSubmission.class))).thenReturn(response);

        mockMvc.perform(post("/api/owner-requests")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.restaurantName").value("Tasty Burgers"));
    }

    @Test
    void shouldReturnBadRequestWhenSubmittingInvalidRequest() throws Exception {
        OwnerRequestSubmission submission = new OwnerRequestSubmission();
        // Missing fields

        mockMvc.perform(post("/api/owner-requests")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetMyRequestsSuccessfully() throws Exception {
        OwnerRequestResponse response = OwnerRequestResponse.builder()
                .id(1L)
                .userId(1L)
                .status(OwnerRequestStatus.PENDING)
                .build();

        when(ownerRequestService.getUserRequests(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/owner-requests/my")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void shouldGetPendingRequestsAsAdminSuccessfully() throws Exception {
        when(ownerRequestService.getAllPendingRequests()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/owner-requests/pending")
                .with(user(adminDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenWhenGettingPendingRequestsAsUser() throws Exception {
        mockMvc.perform(get("/api/owner-requests/pending")
                .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldProcessRequestAsAdminSuccessfully() throws Exception {
        Long requestId = 1L;
        OwnerRequestApproval approval = new OwnerRequestApproval(true, "Looks good!");

        OwnerRequestResponse response = OwnerRequestResponse.builder()
                .id(requestId)
                .status(OwnerRequestStatus.APPROVED)
                .adminNote("Looks good!")
                .build();

        when(ownerRequestService.processRequest(eq(requestId), any(OwnerRequestApproval.class))).thenReturn(response);

        mockMvc.perform(put("/api/owner-requests/{id}/process", requestId)
                .with(user(adminDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.adminNote").value("Looks good!"));
    }

    @Test
    void shouldReturnForbiddenWhenProcessingRequestAsUser() throws Exception {
        OwnerRequestApproval approval = new OwnerRequestApproval(true, "Looks good!");

        mockMvc.perform(put("/api/owner-requests/1/process")
                .with(user(userDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isForbidden());
    }
}

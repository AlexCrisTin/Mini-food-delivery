package com.example.server.controller;

import com.example.server.config.SecurityConfig;
import com.example.server.dto.delivery.*;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.CustomUserDetailsService;
import com.example.server.security.JwtAuthFilter;
import com.example.server.security.JwtUtils;
import com.example.server.service.DeliveryService;
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

import java.math.BigDecimal;
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

@WebMvcTest(DeliveryController.class)
@Import(SecurityConfig.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryService deliveryService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails shipperDetails;

    @BeforeEach
    void setUp() throws Exception {
        shipperDetails = CustomUserDetails.builder()
                .id(1L)
                .email("shipper@test.com")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_SHIPPER")))
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
    void shouldAssignShipperSuccessfully() throws Exception {
        AssignShipperRequest request = new AssignShipperRequest(10L, 1L);

        DeliveryAssignmentResponse response = new DeliveryAssignmentResponse();
        response.setId(100L);

        when(deliveryService.assignShipper(any(AssignShipperRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/deliveries/assign")
                .with(user(shipperDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    void shouldMarkPickedUpSuccessfully() throws Exception {
        MarkPickupRequest request = new MarkPickupRequest();

        mockMvc.perform(patch("/api/deliveries/10/pickup")
                .with(user(shipperDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldMarkDeliveredSuccessfully() throws Exception {
        MarkDeliveredRequest request = new MarkDeliveredRequest();
        request.setCodCollected(true);

        mockMvc.perform(patch("/api/deliveries/10/deliver")
                .with(user(shipperDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateLocationSuccessfully() throws Exception {
        ShipperLocationUpdateRequest request = new ShipperLocationUpdateRequest(new BigDecimal("10.0"), new BigDecimal("10.0"), true);

        mockMvc.perform(put("/api/deliveries/location")
                .with(user(shipperDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetShipperLocationSuccessfully() throws Exception {
        ShipperLocationResponse response = new ShipperLocationResponse();
        response.setLatitude(new BigDecimal("10.0"));

        when(deliveryService.getShipperLocation(eq(1L), eq(1L))).thenReturn(response);

        mockMvc.perform(get("/api/deliveries/1/location")
                .with(user(shipperDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(10.0));
    }

    @Test
    void shouldGetAvailableDeliveries() throws Exception {
        when(deliveryService.getAvailableDeliveries()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/deliveries/available")
                .with(user(shipperDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetMyDeliveries() throws Exception {
        when(deliveryService.getMyDeliveries(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/deliveries/my")
                .with(user(shipperDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetByOrderId() throws Exception {
        DeliveryAssignmentResponse response = new DeliveryAssignmentResponse();
        response.setId(100L);

        when(deliveryService.getByOrderId(eq(10L), eq(1L))).thenReturn(response);

        mockMvc.perform(get("/api/deliveries/order/10")
                .with(user(shipperDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L));
    }
}

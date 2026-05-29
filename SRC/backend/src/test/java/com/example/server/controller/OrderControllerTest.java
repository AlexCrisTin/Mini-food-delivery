package com.example.server.controller;

import com.example.server.config.SecurityConfig;
import com.example.server.dto.order.CreateOrderRequest;
import com.example.server.dto.order.OrderSummaryResponse;
import com.example.server.dto.order.OrderStatusUpdateRequest;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.CustomUserDetailsService;
import com.example.server.security.JwtAuthFilter;
import com.example.server.security.JwtUtils;
import com.example.server.service.OrderService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails customerDetails;

    @BeforeEach
    void setUp() throws Exception {
        customerDetails = CustomUserDetails.builder()
                .id(1L)
                .email("customer@test.com")
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
    void shouldCreateOrderSuccessfully() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setRestaurantId(1L);
        request.setDeliveryAddress("123 Test St");
        request.setDeliveryLat(new java.math.BigDecimal("10.0"));
        request.setDeliveryLng(new java.math.BigDecimal("106.0"));
        request.setPaymentMethod("COD");
        request.setItems(java.util.List.of(new com.example.server.dto.order.CreateOrderItemRequest(1L, 2, "No onion")));

        when(orderService.createOrder(eq(1L), any())).thenReturn(new OrderSummaryResponse());

        mockMvc.perform(post("/api/orders")
                .with(user(customerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(orderService).createOrder(eq(1L), any());
    }

    @Test
    void shouldGetOrderSummarySuccessfully() throws Exception {
        Long orderId = 1L;
        when(orderService.getOrderSummary(eq(orderId), eq(1L))).thenReturn(new OrderSummaryResponse());

        mockMvc.perform(get("/api/orders/{id}", orderId)
                .with(user(customerDetails)))
                .andExpect(status().isOk());

        verify(orderService).getOrderSummary(eq(orderId), eq(1L));
    }

    @Test
    void shouldUpdateOrderStatusSuccessfully() throws Exception {
        Long orderId = 1L;
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest("CONFIRMED", "Confirmed!");

        mockMvc.perform(patch("/api/orders/{id}/status", orderId)
                .with(user(customerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(orderService).updateOrderStatus(eq(orderId), eq(1L), any());
    }
}

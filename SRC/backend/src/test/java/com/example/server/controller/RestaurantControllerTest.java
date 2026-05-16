package com.example.server.controller;

import com.example.server.config.SecurityConfig;
import com.example.server.dto.common.PageResponse;
import com.example.server.dto.restaurant.RestaurantCardResponse;
import com.example.server.dto.restaurant.RestaurantDetailResponse;
import com.example.server.dto.restaurant.RestaurantRequest;
import com.example.server.dto.restaurant.RestaurantSearchRequest;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.CustomUserDetailsService;
import com.example.server.security.JwtAuthFilter;
import com.example.server.security.JwtUtils;
import com.example.server.service.RestaurantService;
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

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
@Import(SecurityConfig.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails ownerDetails;
    private CustomUserDetails customerDetails;

    @BeforeEach
    void setUp() throws Exception {
        ownerDetails = CustomUserDetails.builder()
                .id(1L)
                .email("owner@test.com")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_OWNER")))
                .build();

        customerDetails = CustomUserDetails.builder()
                .id(2L)
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
    void shouldSearchRestaurantsSuccessfully() throws Exception {
        RestaurantSearchRequest request = new RestaurantSearchRequest();
        request.setPage(0);
        request.setSize(10);

        PageResponse<RestaurantCardResponse> response = PageResponse.<RestaurantCardResponse>builder()
                .items(Collections.singletonList(new RestaurantCardResponse()))
                .build();

        when(restaurantService.searchRestaurants(any(RestaurantSearchRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/search")
                .with(user(customerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetRestaurantDetailSuccessfully() throws Exception {
        RestaurantDetailResponse response = new RestaurantDetailResponse();
        response.setId(1L);

        when(restaurantService.getRestaurantDetail(1L)).thenReturn(response);

        mockMvc.perform(get("/api/restaurants/1")
                .with(user(customerDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldGetMyRestaurantsAsOwner() throws Exception {
        when(restaurantService.getMyRestaurants(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/restaurants/my-restaurants")
                .with(user(ownerDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailToGetMyRestaurantsAsCustomer() throws Exception {
        mockMvc.perform(get("/api/restaurants/my-restaurants")
                .with(user(customerDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateRestaurantAsOwner() throws Exception {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("New Res");
        request.setPhone("123456789");
        request.setAddress("Addr");
        request.setCategoryId(1L);
        request.setOpeningTime(LocalTime.of(8, 0));
        request.setClosingTime(LocalTime.of(22, 0));

        RestaurantDetailResponse response = new RestaurantDetailResponse();
        response.setId(10L);

        when(restaurantService.createRestaurant(eq(1L), any(RestaurantRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants")
                .with(user(ownerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void shouldFailToCreateRestaurantAsCustomer() throws Exception {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("New Res");
        request.setPhone("123456789");
        request.setAddress("Addr");
        request.setCategoryId(1L);
        request.setOpeningTime(LocalTime.of(8, 0));
        request.setClosingTime(LocalTime.of(22, 0));

        mockMvc.perform(post("/api/restaurants")
                .with(user(customerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateRestaurantAsOwner() throws Exception {
        RestaurantRequest request = new RestaurantRequest();
        request.setName("Updated Res");
        request.setPhone("123456789");
        request.setAddress("Addr");
        request.setCategoryId(1L);
        request.setOpeningTime(LocalTime.of(8, 0));
        request.setClosingTime(LocalTime.of(22, 0));

        when(restaurantService.updateRestaurant(eq(1L), eq(10L), any(RestaurantRequest.class)))
                .thenReturn(new RestaurantDetailResponse());

        mockMvc.perform(put("/api/restaurants/10")
                .with(user(ownerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteRestaurantAsOwner() throws Exception {
        mockMvc.perform(delete("/api/restaurants/10")
                .with(user(ownerDetails))
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

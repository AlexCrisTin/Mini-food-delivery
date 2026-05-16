package com.example.server.controller;

import com.example.server.config.SecurityConfig;
import com.example.server.dto.restaurant.MenuCategoryRequest;
import com.example.server.dto.restaurant.MenuCategoryResponse;
import com.example.server.dto.restaurant.MenuItemRequest;
import com.example.server.dto.restaurant.MenuItemResponse;
import com.example.server.security.CustomUserDetails;
import com.example.server.security.CustomUserDetailsService;
import com.example.server.security.JwtAuthFilter;
import com.example.server.security.JwtUtils;
import com.example.server.service.MenuService;
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

@WebMvcTest(MenuController.class)
@Import(SecurityConfig.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuService menuService;

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
    void shouldGetMenuCategoriesSuccessfully() throws Exception {
        when(menuService.getMenuCategories(10L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/restaurants/10/menu/categories")
                .with(user(customerDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddMenuCategorySuccessfully() throws Exception {
        MenuCategoryRequest request = new MenuCategoryRequest("Drinks");
        MenuCategoryResponse response = new MenuCategoryResponse();
        response.setId(100L);

        when(menuService.addMenuCategory(eq(1L), eq(10L), any(MenuCategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/10/menu/categories")
                .with(user(ownerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    void shouldUpdateMenuCategorySuccessfully() throws Exception {
        MenuCategoryRequest request = new MenuCategoryRequest("Snacks");

        when(menuService.updateMenuCategory(eq(1L), eq(100L), any(MenuCategoryRequest.class)))
                .thenReturn(new MenuCategoryResponse());

        mockMvc.perform(put("/api/restaurants/10/menu/categories/100")
                .with(user(ownerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteMenuCategorySuccessfully() throws Exception {
        mockMvc.perform(delete("/api/restaurants/10/menu/categories/100")
                .with(user(ownerDetails))
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldAddMenuItemSuccessfully() throws Exception {
        MenuItemRequest request = new MenuItemRequest(100L, "Cola", "Cold drink", new BigDecimal("1.5"), null, true);
        MenuItemResponse response = new MenuItemResponse();
        response.setId(200L);

        when(menuService.addMenuItem(eq(1L), eq(10L), eq(100L), any(MenuItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants/10/menu/categories/100/items")
                .with(user(ownerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(200L));
    }

    @Test
    void shouldUpdateMenuItemSuccessfully() throws Exception {
        MenuItemRequest request = new MenuItemRequest(100L, "Cola", "Cold drink", new BigDecimal("1.5"), null, true);

        when(menuService.updateMenuItem(eq(1L), eq(200L), any(MenuItemRequest.class))).thenReturn(new MenuItemResponse());

        mockMvc.perform(put("/api/restaurants/10/menu/items/200")
                .with(user(ownerDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteMenuItemSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/restaurants/10/menu/items/200")
                .with(user(ownerDetails))
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetMenuItemSuccessfully() throws Exception {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(200L);

        when(menuService.getMenuItem(200L)).thenReturn(response);

        mockMvc.perform(get("/api/restaurants/10/menu/items/200")
                .with(user(customerDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200L));
    }
}

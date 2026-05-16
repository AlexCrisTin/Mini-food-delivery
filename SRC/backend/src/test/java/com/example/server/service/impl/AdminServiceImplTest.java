package com.example.server.service.impl;

import com.example.server.dto.report.AdminStatsResponse;
import com.example.server.dto.restaurant.RestaurantApprovalRequest;
import com.example.server.dto.restaurant.RestaurantCardResponse;
import com.example.server.dto.user.UserProfileResponse;
import com.example.server.dto.user.UserRoleUpdateRequest;
import com.example.server.dto.user.UserStatusUpdateRequest;
import com.example.server.entity.Restaurant;
import com.example.server.entity.User;
import com.example.server.enums.Role;
import com.example.server.mapper.RestaurantMapper;
import com.example.server.mapper.UserMapper;
import com.example.server.repository.OrderRepository;
import com.example.server.repository.RestaurantRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.NotificationService;
import com.example.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Spy
    private RestaurantMapper restaurantMapper = Mappers.getMapper(RestaurantMapper.class);

    @InjectMocks
    private AdminServiceImpl adminService;

    private Restaurant restaurant;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setFullName("Owner");

        restaurant = new Restaurant();
        restaurant.setId(10L);
        restaurant.setName("Test Restaurant");
        restaurant.setOwner(owner);
    }

    @Test
    void shouldApproveRestaurantSuccessfully() {
        RestaurantApprovalRequest request = new RestaurantApprovalRequest(true, "Looks good");
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));

        adminService.approveRestaurant(10L, request);

        assertTrue(restaurant.getIsApproved());
        verify(restaurantRepository).save(restaurant);
        verify(notificationService).createNotification(eq(1L), anyString(), contains("approved"), eq("SYSTEM"));
    }

    @Test
    void shouldUpdateUserRole() {
        UserRoleUpdateRequest request = new UserRoleUpdateRequest();
        request.setRole(Role.ROLE_OWNER);
        
        adminService.updateUserRole(1L, request);
        
        verify(userService).updateUserRole(1L, request);
    }

    @Test
    void shouldUpdateUserStatus() {
        UserStatusUpdateRequest request = new UserStatusUpdateRequest();
        request.setActive(false);
        
        adminService.updateUserStatus(1L, request);
        
        verify(userService).updateUserStatus(1L, request);
    }

    @Test
    void shouldGetAllUsers() {
        User user = new User();
        user.setFullName("User A");
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserProfileResponse> result = adminService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetPendingRestaurants() {
        when(restaurantRepository.findByIsApprovedFalseAndIsDeletedFalse())
                .thenReturn(Collections.singletonList(restaurant));

        List<RestaurantCardResponse> result = adminService.getPendingRestaurants();

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetSystemStats() {
        when(userRepository.count()).thenReturn(100L);
        when(restaurantRepository.count()).thenReturn(10L);
        when(restaurantRepository.findByIsApprovedFalseAndIsDeletedFalse()).thenReturn(Collections.emptyList());
        when(orderRepository.count()).thenReturn(50L);
        when(orderRepository.sumTotalRevenue(any(), any())).thenReturn(new BigDecimal("1000.00"));

        AdminStatsResponse stats = adminService.getSystemStats();

        assertEquals(100L, stats.getTotalUsers());
        assertEquals(1000.0, stats.getTotalRevenue());
    }

    @Test
    void shouldDeleteUser() {
        adminService.deleteUser(1L);
        verify(userService).deleteUser(1L);
    }
}

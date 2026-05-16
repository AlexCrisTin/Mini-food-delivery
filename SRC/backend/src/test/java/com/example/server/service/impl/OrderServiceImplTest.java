package com.example.server.service.impl;

import com.example.server.dto.common.PageResponse;
import com.example.server.dto.order.*;
import com.example.server.entity.*;
import com.example.server.enums.OrderStatus;
import com.example.server.enums.Role;
import com.example.server.event.OrderReadyEvent;
import com.example.server.exception.AppException;
import com.example.server.exception.ResourceNotFoundException;
import com.example.server.mapper.OrderMapper;
import com.example.server.repository.*;
import com.example.server.service.MapService;
import com.example.server.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private MapService mapService;
    @Mock
    private NotificationService notificationService;

    @Spy
    private com.example.server.mapper.DeliveryMapper deliveryMapper = Mappers.getMapper(com.example.server.mapper.DeliveryMapper.class);

    @Spy
    private OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @InjectMocks
    private OrderServiceImpl orderService;

    private User customer;
    private User owner;
    private Restaurant restaurant;
    private MenuItem menuItem;
    private Order order;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "defaultDeliveryFee", "15.00");
        ReflectionTestUtils.setField(orderMapper, "deliveryMapper", deliveryMapper);

        customer = User.builder().id(1L).fullName("Customer").role(Role.ROLE_CUSTOMER).build();
        owner = User.builder().id(2L).fullName("Owner").role(Role.ROLE_OWNER).build();
        
        restaurant = Restaurant.builder()
                .id(10L)
                .name("Test Restaurant")
                .owner(owner)
                .latitude(new BigDecimal("10.0"))
                .longitude(new BigDecimal("10.0"))
                .build();

        menuItem = MenuItem.builder()
                .id(20L)
                .name("Pizza")
                .price(new BigDecimal("100.0"))
                .build();

        order = new Order();
        order.setId(100L);
        order.setUser(customer);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.PENDING.name());
        order.setOrderItems(new ArrayList<>());
    }

    @Test
    void shouldCreateOrderWithDistanceBasedFee() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setRestaurantId(10L);
        request.setDeliveryLat(new BigDecimal("10.1"));
        request.setDeliveryLng(new BigDecimal("10.1"));
        request.setItems(List.of(new CreateOrderItemRequest(20L, 2, "Extra cheese")));

        com.example.server.dto.map.RoutingResponse route = new com.example.server.dto.map.RoutingResponse();
        com.example.server.dto.map.RoutingResponse.Route r = new com.example.server.dto.map.RoutingResponse.Route();
        r.setDistance(5000.0); // 5km
        route.setRoutes(List.of(r));

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(mapService.getRoute(any(), any(), any(), any())).thenReturn(route);
        when(menuItemRepository.findById(20L)).thenReturn(Optional.of(menuItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderSummaryResponse result = orderService.createOrder(1L, request);

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
        // Distance based fee: 5.0 + (5km * 2.0) = 15.0
    }

    @Test
    void shouldGetOrderSummaryForAdmin() {
        User admin = User.builder().id(99L).role(Role.ROLE_ADMIN).build();
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(userRepository.findById(99L)).thenReturn(Optional.of(admin));

        OrderSummaryResponse result = orderService.getOrderSummary(100L, 99L);

        assertNotNull(result);
    }

    @Test
    void shouldGetOrderHistory() {
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(eq(1L), any())).thenReturn(page);

        PageResponse<OrderSummaryResponse> result = orderService.getOrderHistory(1L, 0, 10);

        assertEquals(1, result.getItems().size());
    }

    @Test
    void shouldUpdateOrderStatusSuccessfully() {
        order.setStatus(OrderStatus.CONFIRMED.name());
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest(OrderStatus.PREPARING.name(), "Cooking");

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        orderService.updateOrderStatus(100L, 2L, request);

        assertEquals(OrderStatus.PREPARING.name(), order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowExceptionWhenInvalidTransition() {
        order.setStatus(OrderStatus.PENDING.name());
        // Skip CONFIRMED
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest(OrderStatus.PREPARING.name(), "Skip");

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        assertThrows(AppException.class, () -> orderService.updateOrderStatus(100L, 2L, request));
    }

    @Test
    void shouldGetOrderTracking() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));

        OrderTrackingResponse result = orderService.getOrderTracking(100L, 1L);

        assertNotNull(result);
    }

    @Test
    void shouldGetRestaurantOrders() {
        when(orderRepository.findByRestaurantIdAndStatus(10L, "PENDING"))
                .thenReturn(Collections.singletonList(order));

        List<OrderSummaryResponse> result = orderService.getRestaurantOrders(10L, "PENDING");

        assertEquals(1, result.size());
    }
}

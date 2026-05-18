package com.example.server.mapper;

import com.example.server.dto.order.*;
import com.example.server.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(mapper, "deliveryMapper", Mappers.getMapper(DeliveryMapper.class));
    }

    @Test
    void shouldMapToSummaryResponse() {
        User user = new User();
        user.setId(1L);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(2L);
        restaurant.setName("Tasty Food");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setTotalAmount(new BigDecimal("50.00"));

        OrderSummaryResponse response = mapper.toSummaryResponse(order);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(2L, response.getRestaurantId());
        assertEquals("Tasty Food", response.getRestaurantName());
        assertEquals(new BigDecimal("50.00"), response.getTotalAmount());
    }

    @Test
    void shouldMapToSummaryResponseWithNulls() {
        assertNull(mapper.toSummaryResponse(null));
    }

    @Test
    void shouldMapToItemResponse() {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(5L);
        menuItem.setName("Burger");
        menuItem.setPrice(new BigDecimal("10.00"));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(10L);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setSubtotal(new BigDecimal("20.00"));

        OrderItemResponse response = mapper.toItemResponse(orderItem);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(5L, response.getMenuItemId());
        assertEquals("Burger", response.getItemName());
        assertEquals(new BigDecimal("10.00"), response.getItemPrice());
        assertEquals(2, response.getQuantity());
        assertEquals(new BigDecimal("20.00"), response.getSubtotal());
    }

    @Test
    void shouldMapToItemResponseWithNulls() {
        assertNull(mapper.toItemResponse(null));
    }

    @Test
    void shouldMapToTrackingResponse() {
        Order order = new Order();
        order.setId(100L);
        order.setStatus("SHIPPING");

        OrderStatusHistory history = new OrderStatusHistory();
        history.setId(1L);
        history.setStatus("SHIPPING");
        history.setChangedBy(new User());

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setId(5L);

        order.setStatusHistories(List.of(history));
        order.setDeliveryAssignment(assignment);

        OrderTrackingResponse response = mapper.toTrackingResponse(order);

        assertNotNull(response);
        assertEquals(100L, response.getOrderId());
        assertEquals("SHIPPING", response.getStatus());
        assertNotNull(response.getTimeline());
        assertEquals(1, response.getTimeline().size());
        assertNotNull(response.getAssignment());
        assertEquals(5L, response.getAssignment().getId());
    }

    @Test
    void shouldMapToTrackingResponseWithNulls() {
        assertNull(mapper.toTrackingResponse(null));
    }

    @Test
    void shouldMapToStatusHistoryResponse() {
        Order order = new Order();
        order.setId(100L);

        User changedBy = new User();
        changedBy.setId(1L);
        changedBy.setFullName("Admin");

        OrderStatusHistory history = new OrderStatusHistory();
        history.setId(5L);
        history.setOrder(order);
        history.setChangedBy(changedBy);
        history.setStatus("CONFIRMED");

        OrderStatusHistoryResponse response = mapper.toStatusHistoryResponse(history);

        assertNotNull(response);
        assertEquals(5L, response.getId());
        assertEquals(100L, response.getOrderId());
        assertEquals(1L, response.getChangedByUserId());
        assertEquals("Admin", response.getChangedByName());
        assertEquals("CONFIRMED", response.getStatus());
    }

    @Test
    void shouldMapToStatusHistoryResponseWithNulls() {
        assertNull(mapper.toStatusHistoryResponse(null));
    }
}

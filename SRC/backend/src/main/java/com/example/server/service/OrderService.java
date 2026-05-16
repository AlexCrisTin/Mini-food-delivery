package com.example.server.service;

import com.example.server.dto.common.PageResponse;
import com.example.server.dto.order.*;
import java.util.List;

public interface OrderService {
    OrderSummaryResponse createOrder(Long userId, CreateOrderRequest request);
    OrderSummaryResponse getOrderSummary(Long id, Long requesterId);

    PageResponse<OrderSummaryResponse> getOrderHistory(Long userId, int page, int size);

    void updateOrderStatus(Long orderId, Long userId, OrderStatusUpdateRequest request);

    OrderTrackingResponse getOrderTracking(Long orderId, Long requesterId);

    List<OrderSummaryResponse> getRestaurantOrders(Long restaurantId, String status, Long requesterId, String requesterRole);
}

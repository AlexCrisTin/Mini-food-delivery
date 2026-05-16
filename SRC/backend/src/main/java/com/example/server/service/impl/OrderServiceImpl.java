package com.example.server.service.impl;

import com.example.server.dto.common.PageResponse;
import com.example.server.dto.order.*;
import com.example.server.entity.*;
import com.example.server.enums.OrderStatus;
import com.example.server.event.OrderReadyEvent;
import com.example.server.exception.AppException;
import com.example.server.exception.ResourceNotFoundException;
import com.example.server.mapper.OrderMapper;
import com.example.server.repository.*;
import com.example.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class OrderServiceImpl implements OrderService {

    private static final String RESOURCE_NAME = "Order";

    @Value("${app.delivery.default-fee:15.00}")
    private String defaultDeliveryFee;

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final com.example.server.service.MapService mapService;
    private final com.example.server.service.NotificationService notificationService;

    @Override
    public OrderSummaryResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));

        // 1. Calculate delivery fee (EXTERNAL API CALL - OUTSIDE TRANSACTION)
        BigDecimal deliveryFee = calculateDeliveryFee(restaurant, request.getDeliveryLat(), request.getDeliveryLng());

        // 2. Persist order (INSIDE TRANSACTION)
        OrderSummaryResponse response = persistOrder(user, restaurant, deliveryFee, request);

        // 3. Notify Restaurant Owner
        notificationService.createNotification(
                restaurant.getOwner().getId(),
                "New Order Received",
                "You have a new order #" + response.getId() + " from " + user.getFullName(),
                "NEW_ORDER");

        return response;
    }

    private BigDecimal calculateDeliveryFee(Restaurant restaurant, BigDecimal deliveryLat, BigDecimal deliveryLng) {
        BigDecimal deliveryFee = new BigDecimal(defaultDeliveryFee);
        try {
            com.example.server.dto.map.RoutingResponse route = mapService.getRoute(
                    restaurant.getLatitude(), restaurant.getLongitude(),
                    deliveryLat, deliveryLng);
            if (route != null && !route.getRoutes().isEmpty()) {
                double distanceKm = route.getRoutes().get(0).getDistance() / 1000.0;
                // Example: 5.00 base + 2.00 per km
                deliveryFee = BigDecimal.valueOf(5.00 + (distanceKm * 2.00))
                        .setScale(2, java.math.RoundingMode.HALF_UP);
            }
        } catch (Exception e) {
            log.error("Failed to calculate distance-based fee, using default: {}", e.getMessage());
        }
        return deliveryFee;
    }

    @Transactional
    public OrderSummaryResponse persistOrder(User user, Restaurant restaurant, BigDecimal deliveryFee,
            CreateOrderRequest request) {
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryLat(request.getDeliveryLat());
        order.setDeliveryLng(request.getDeliveryLng());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order.setStatus(OrderStatus.PENDING.name());
        order.setDeliveryFee(deliveryFee);

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> items = request.getItems().stream().map(itemRequest -> {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemRequest.getMenuItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setItemName(menuItem.getName());
            orderItem.setItemPrice(menuItem.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setSubtotal(menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            orderItem.setNote(itemRequest.getNote());
            return orderItem;
        }).toList();

        for (OrderItem item : items) {
            subtotal = subtotal.add(item.getSubtotal());
        }

        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal.add(order.getDeliveryFee()));
        order.setOrderItems(items);

        Order savedOrder = orderRepository.save(order);

        // Add initial status history
        addStatusHistory(savedOrder, user, OrderStatus.PENDING.name(), "Order created");

        return orderMapper.toSummaryResponse(savedOrder);
    }

    @Override
    public OrderSummaryResponse getOrderSummary(Long id, Long requesterId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "id", id));

        validateOrderOwnership(order, requesterId);

        return orderMapper.toSummaryResponse(order);
    }

    private void validateOrderOwnership(Order order, Long requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requesterId));

        // Admin can see everything
        if (com.example.server.enums.Role.ROLE_ADMIN.equals(requester.getRole())) {
            return;
        }

        // Customer can see their own orders
        if (order.getUser().getId().equals(requesterId)) {
            return;
        }

        // Restaurant owner can see orders for their restaurant
        if (order.getRestaurant().getOwner().getId().equals(requesterId)) {
            return;
        }

        // Shipper can see orders assigned to them
        if (order.getDeliveryAssignment() != null &&
                order.getDeliveryAssignment().getShipper() != null &&
                order.getDeliveryAssignment().getShipper().getId().equals(requesterId)) {
            return;
        }

        throw new AppException(HttpStatus.FORBIDDEN, "You do not have permission to access this order",
                "UNAUTHORIZED_ACCESS");
    }

    @Override
    public PageResponse<OrderSummaryResponse> getOrderHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return PageResponse.<OrderSummaryResponse>builder()
                .items(orderPage.getContent().stream()
                        .map(orderMapper::toSummaryResponse)
                        .collect(Collectors.toList()))
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, Long userId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "id", orderId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Ownership check for status update
        validateStatusUpdatePermission(order, user, request.getStatus());

        String oldStatus = order.getStatus();
        String newStatus = request.getStatus();

        validateStateTransition(oldStatus, newStatus);

        order.setStatus(newStatus);
        orderRepository.save(order);

        addStatusHistory(order, user, newStatus, request.getNote());

        // Notify Customer of status change
        notificationService.createNotification(
                order.getUser().getId(),
                "Order Status Updated",
                "Your order #" + order.getId() + " is now " + newStatus,
                "ORDER_STATUS_UPDATE"
        );

        // Publish event if ready for delivery
        if (OrderStatus.READY.name().equals(newStatus)) {
            eventPublisher.publishEvent(new OrderReadyEvent(this, order.getId()));
        }
    }

    private void validateStatusUpdatePermission(Order order, User user, String statusStr) {
        String role = user.getRole();

        if (com.example.server.enums.Role.ROLE_ADMIN.equals(role)) {
            return;
        }

        OrderStatus status = parseStatus(statusStr);

        if (status == OrderStatus.CANCELLED) {
            // Only customer who placed the order or admin can cancel (usually)
            if (order.getUser().getId().equals(user.getId())) {
                return;
            }
        }

        if (status == OrderStatus.CONFIRMED || status == OrderStatus.PREPARING || status == OrderStatus.READY
                || status == OrderStatus.REJECTED) {
            // Only restaurant owner can update to these statuses
            if (order.getRestaurant().getOwner().getId().equals(user.getId())) {
                return;
            }
        }

        if (status == OrderStatus.SHIPPING || status == OrderStatus.DELIVERED) {
            // Only assigned shipper can update to these statuses
            if (order.getDeliveryAssignment() != null &&
                    order.getDeliveryAssignment().getShipper() != null &&
                    order.getDeliveryAssignment().getShipper().getId().equals(user.getId())) {
                return;
            }
        }

        throw new AppException(HttpStatus.FORBIDDEN, "You do not have permission to update this order status",
                "UNAUTHORIZED_ACCESS");
    }

    private void validateStateTransition(String oldStatus, String newStatus) {
        OrderStatus current = parseStatus(oldStatus);
        OrderStatus next = parseStatus(newStatus);

        if (next == OrderStatus.CANCELLED || next == OrderStatus.REJECTED) {
            if (current == OrderStatus.SHIPPING || current == OrderStatus.DELIVERED) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        "Cannot cancel/reject an order that is already shipping or delivered", "INVALID_TRANSITION");
            }
            return;
        }

        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED;
            case CONFIRMED -> next == OrderStatus.PREPARING;
            case PREPARING -> next == OrderStatus.READY;
            case READY -> next == OrderStatus.SHIPPING;
            case SHIPPING -> next == OrderStatus.DELIVERED;
            default -> false;
        };

        if (!valid) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Invalid order status transition from " + oldStatus + " to " + newStatus, "INVALID_TRANSITION");
        }
    }

    private OrderStatus parseStatus(String statusStr) {
        if (statusStr == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Order status cannot be null", "INVALID_STATUS");
        }
        try {
            return OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid order status: " + statusStr, "INVALID_STATUS");
        }
    }

    @Override
    public OrderTrackingResponse getOrderTracking(Long orderId, Long requesterId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "id", orderId));

        validateOrderOwnership(order, requesterId);

        return orderMapper.toTrackingResponse(order);
    }

    @Override
    public List<OrderSummaryResponse> getRestaurantOrders(Long restaurantId, String status) {
        return orderRepository.findByRestaurantIdAndStatus(restaurantId, status).stream()
                .map(orderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    private void addStatusHistory(Order order, User user, String status, String note) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setChangedBy(user);
        history.setStatus(status);
        history.setNote(note);
        orderStatusHistoryRepository.save(history);
    }
}

package com.example.server.controller;

import com.example.server.dto.common.PageResponse;
import com.example.server.dto.order.CreateOrderRequest;
import com.example.server.dto.order.OrderSummaryResponse;
import com.example.server.dto.order.OrderStatusUpdateRequest;
import com.example.server.dto.order.OrderTrackingResponse;
import com.example.server.security.CustomUserDetails;
import com.example.server.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderSummaryResponse> createOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userDetails.getId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderSummary(id, userDetails.getId()));
    }

    @GetMapping("/history")
    public ResponseEntity<PageResponse<OrderSummaryResponse>> getOrderHistory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getOrderHistory(userDetails.getId(), page, size));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody OrderStatusUpdateRequest request) {
        orderService.updateOrderStatus(id, userDetails.getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<OrderTrackingResponse> getOrderTracking(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                 @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderTracking(id, userDetails.getId()));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<OrderSummaryResponse>> getRestaurantOrders(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                          @PathVariable Long restaurantId,
                                                                          @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId, status, userDetails.getId(), userDetails.getRole()));
    }
}

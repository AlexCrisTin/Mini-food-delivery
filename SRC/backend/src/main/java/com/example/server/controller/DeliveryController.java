package com.example.server.controller;

import com.example.server.dto.delivery.*;
import com.example.server.security.CustomUserDetails;
import com.example.server.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHIPPER')")
    public ResponseEntity<DeliveryAssignmentResponse> assignShipper(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                     @Valid @RequestBody AssignShipperRequest request) {
        return ResponseEntity.ok(deliveryService.assignShipper(userDetails.getId(), userDetails.getRole(), request));
    }

    @PatchMapping("/{orderId}/pickup")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<Void> markPickedUp(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable Long orderId,
                                             @Valid @RequestBody MarkPickupRequest request) {
        deliveryService.markPickedUp(userDetails.getId(), orderId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{orderId}/deliver")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<Void> markDelivered(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long orderId,
                                              @Valid @RequestBody MarkDeliveredRequest request) {
        deliveryService.markDelivered(userDetails.getId(), orderId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/location")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<Void> updateLocation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @Valid @RequestBody ShipperLocationUpdateRequest request) {
        deliveryService.updateLocation(userDetails.getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{shipperId}/location")
    public ResponseEntity<ShipperLocationResponse> getShipperLocation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                     @PathVariable Long shipperId) {
        return ResponseEntity.ok(deliveryService.getShipperLocation(shipperId, userDetails.getId()));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('SHIPPER', 'ADMIN')")
    public ResponseEntity<List<DeliveryAssignmentResponse>> getAvailableDeliveries() {
        return ResponseEntity.ok(deliveryService.getAvailableDeliveries());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<List<DeliveryAssignmentResponse>> getMyDeliveries(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(deliveryService.getMyDeliveries(userDetails.getId()));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeliveryAssignmentResponse> getByOrderId(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  @PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getByOrderId(orderId, userDetails.getId()));
    }
}

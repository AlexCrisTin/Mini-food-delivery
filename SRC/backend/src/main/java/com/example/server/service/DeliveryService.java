package com.example.server.service;

import com.example.server.dto.delivery.*;

import java.util.List;

public interface DeliveryService {
    void createUnassignedAssignment(Long orderId);
    DeliveryAssignmentResponse assignShipper(Long requesterId, String requesterRole, AssignShipperRequest request);
    void markPickedUp(Long shipperId, Long orderId, MarkPickupRequest request);
    void markDelivered(Long shipperId, Long orderId, MarkDeliveredRequest request);
    void updateLocation(Long shipperId, ShipperLocationUpdateRequest request);
    ShipperLocationResponse getShipperLocation(Long shipperId, Long requesterId);

    List<DeliveryAssignmentResponse> getAvailableDeliveries();

    List<DeliveryAssignmentResponse> getMyDeliveries(Long shipperId);

    DeliveryAssignmentResponse getByOrderId(Long orderId, Long requesterId);

}

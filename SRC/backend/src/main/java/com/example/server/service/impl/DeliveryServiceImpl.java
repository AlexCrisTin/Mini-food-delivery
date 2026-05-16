package com.example.server.service.impl;

import com.example.server.dto.delivery.*;
import com.example.server.entity.*;
import com.example.server.enums.DeliveryAssignmentStatus;
import com.example.server.enums.OrderStatus;
import com.example.server.enums.Role;
import com.example.server.exception.AppException;
import com.example.server.exception.ResourceNotFoundException;
import com.example.server.mapper.DeliveryMapper;
import com.example.server.repository.*;
import com.example.server.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private static final String RESOURCE_ORDER = "Order";
    private static final String RESOURCE_USER = "User";
    private static final String RESOURCE_ASSIGNMENT = "DeliveryAssignment";
    private static final String RESOURCE_LOCATION = "ShipperLocation";

    private final DeliveryAssignmentRepository deliveryAssignmentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShipperLocationRepository shipperLocationRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final DeliveryMapper deliveryMapper;
    private final com.example.server.service.NotificationService notificationService;

    @Override
    @Transactional
    public void createUnassignedAssignment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ORDER, "id", orderId));

        if (deliveryAssignmentRepository.findByOrderId(orderId).isPresent()) {
            return; // Already exists
        }

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrder(order);
        assignment.setStatus(DeliveryAssignmentStatus.UNASSIGNED.name());
        deliveryAssignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public DeliveryAssignmentResponse assignShipper(AssignShipperRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ORDER, "id", request.getOrderId()));

        User shipper = userRepository.findById(request.getShipperId())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_USER, "id", request.getShipperId()));

        if (!Role.ROLE_SHIPPER.equals(shipper.getRole())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "User is not a shipper", "INVALID_ROLE");
        }

        DeliveryAssignment assignment = deliveryAssignmentRepository.findByOrderId(request.getOrderId())
                .orElseGet(() -> {
                    DeliveryAssignment newAssignment = new DeliveryAssignment();
                    newAssignment.setOrder(order);
                    return newAssignment;
                });

        assignment.setShipper(shipper);
        assignment.setStatus(DeliveryAssignmentStatus.ASSIGNED.name());

        // Update order status to SHIPPING if it was READY
        if (OrderStatus.READY.name().equals(order.getStatus())) {
            order.setStatus(OrderStatus.SHIPPING.name());
            orderRepository.save(order);
            addStatusHistory(order, shipper, OrderStatus.SHIPPING.name(), "Shipper assigned");
        }

        // Notify Customer
        notificationService.createNotification(
                order.getUser().getId(),
                "Shipper Assigned",
                "Shipper " + shipper.getFullName() + " has been assigned to your order #" + order.getId(),
                "SHIPPER_ASSIGNED"
        );

        // Notify Shipper
        notificationService.createNotification(
                shipper.getId(),
                "New Delivery Assigned",
                "You have been assigned to deliver order #" + order.getId(),
                "NEW_DELIVERY"
        );

        return deliveryMapper.toResponse(deliveryAssignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public void markPickedUp(Long shipperId, Long orderId, MarkPickupRequest request) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ASSIGNMENT, "orderId", orderId));

        if (!assignment.getShipper().getId().equals(shipperId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "Unauthorized to update this assignment", "UNAUTHORIZED_UPDATE");
        }

        if (!DeliveryAssignmentStatus.ASSIGNED.name().equals(assignment.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Assignment must be in ASSIGNED status to be picked up", "INVALID_STATE");
        }

        assignment.setStatus(DeliveryAssignmentStatus.PICKED_UP.name());
        assignment.setPickedUpAt(LocalDateTime.now());
        deliveryAssignmentRepository.save(assignment);

        addStatusHistory(assignment.getOrder(), assignment.getShipper(), "PICKED_UP", "Order picked up by shipper");

        // Notify Customer
        notificationService.createNotification(
                assignment.getOrder().getUser().getId(),
                "Order Picked Up",
                "Your order #" + orderId + " has been picked up by the shipper and is on the way.",
                "ORDER_PICKED_UP"
        );
    }

    @Override
    @Transactional
    public void markDelivered(Long shipperId, Long orderId, MarkDeliveredRequest request) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ASSIGNMENT, "orderId", orderId));

        if (!assignment.getShipper().getId().equals(shipperId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "Unauthorized to update this assignment", "UNAUTHORIZED_UPDATE");
        }

        if (!DeliveryAssignmentStatus.PICKED_UP.name().equals(assignment.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Assignment must be in PICKED_UP status to be delivered", "INVALID_STATE");
        }

        if (Boolean.FALSE.equals(request.getCodCollected())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "COD must be collected to mark as delivered", "COD_NOT_COLLECTED");
        }

        assignment.setStatus(DeliveryAssignmentStatus.DELIVERED.name());
        assignment.setDeliveredAt(LocalDateTime.now());
        deliveryAssignmentRepository.save(assignment);

        // Update order status to DELIVERED
        Order order = assignment.getOrder();
        order.setStatus(OrderStatus.DELIVERED.name());
        order.setIsPaid(true);
        orderRepository.save(order);

        addStatusHistory(order, assignment.getShipper(), OrderStatus.DELIVERED.name(), "Order delivered");

        // Notify Customer
        notificationService.createNotification(
                order.getUser().getId(),
                "Order Delivered",
                "Your order #" + orderId + " has been successfully delivered. Enjoy your meal!",
                "ORDER_DELIVERED"
        );
    }

    private void addStatusHistory(Order order, User user, String status, String note) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setChangedBy(user);
        history.setStatus(status);
        history.setNote(note);
        orderStatusHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void updateLocation(Long shipperId, ShipperLocationUpdateRequest request) {
        ShipperLocation location = shipperLocationRepository.findByShipperId(shipperId)
                .orElseGet(() -> {
                    User shipper = userRepository.findById(shipperId)
                            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_USER, "id", shipperId));
                    ShipperLocation newLoc = new ShipperLocation();
                    newLoc.setShipper(shipper);
                    return newLoc;
                });

        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setIsOnline(request.getIsOnline());
        shipperLocationRepository.save(location);
    }

    @Override
    public ShipperLocationResponse getShipperLocation(Long shipperId, Long requesterId) {
        ShipperLocation location = shipperLocationRepository.findByShipperId(shipperId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_LOCATION, "shipperId", shipperId));
        
        validateLocationAccess(shipperId, requesterId);

        return deliveryMapper.toLocationResponse(location);
    }

    private void validateLocationAccess(Long shipperId, Long requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_USER, "id", requesterId));

        if (Role.ROLE_ADMIN.equals(requester.getRole())) {
            return;
        }

        if (shipperId.equals(requesterId)) {
            return;
        }

        // Check if requester is a customer with an active order being delivered by this shipper
        boolean isAssociatedCustomer = deliveryAssignmentRepository.findAllByShipperId(shipperId).stream()
                .anyMatch(a -> a.getOrder().getUser().getId().equals(requesterId) && 
                              (DeliveryAssignmentStatus.ASSIGNED.name().equals(a.getStatus()) || 
                               DeliveryAssignmentStatus.PICKED_UP.name().equals(a.getStatus())));
        
        if (isAssociatedCustomer) {
            return;
        }

        throw new AppException(HttpStatus.FORBIDDEN, "You do not have permission to view this shipper's location", "UNAUTHORIZED_ACCESS");
    }

    @Override
    public List<DeliveryAssignmentResponse> getAvailableDeliveries() {
        return deliveryAssignmentRepository.findAllByShipperIsNullAndStatus("UNASSIGNED")
                .stream()
                .map(deliveryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryAssignmentResponse> getMyDeliveries(Long shipperId) {
        return deliveryAssignmentRepository.findAllByShipperId(shipperId)
                .stream()
                .map(deliveryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeliveryAssignmentResponse getByOrderId(Long orderId, Long requesterId) {
        DeliveryAssignment assignment = deliveryAssignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ASSIGNMENT, "orderId", orderId));
        
        validateAssignmentAccess(assignment, requesterId);

        return deliveryMapper.toResponse(assignment);
    }

    private void validateAssignmentAccess(DeliveryAssignment assignment, Long requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_USER, "id", requesterId));

        if (Role.ROLE_ADMIN.equals(requester.getRole())) {
            return;
        }

        if (assignment.getShipper() != null && assignment.getShipper().getId().equals(requesterId)) {
            return;
        }

        if (assignment.getOrder().getUser().getId().equals(requesterId)) {
            return;
        }

        throw new AppException(HttpStatus.FORBIDDEN, "You do not have permission to view this delivery assignment", "UNAUTHORIZED_ACCESS");
    }
}

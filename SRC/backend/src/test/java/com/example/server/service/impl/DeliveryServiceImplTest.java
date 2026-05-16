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
import com.example.server.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryAssignmentRepository deliveryAssignmentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShipperLocationRepository shipperLocationRepository;
    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;
    @Mock
    private NotificationService notificationService;

    @Spy
    private DeliveryMapper deliveryMapper = Mappers.getMapper(DeliveryMapper.class);

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private User shipper;
    private User customer;
    private Order order;
    private DeliveryAssignment assignment;
    private final Long shipperId = 1L;
    private final Long orderId = 10L;

    @BeforeEach
    void setUp() {
        shipper = new User();
        shipper.setId(shipperId);
        shipper.setRole(Role.ROLE_SHIPPER);
        shipper.setFullName("Test Shipper");

        customer = new User();
        customer.setId(2L);
        customer.setFullName("Customer");
        customer.setRole(Role.ROLE_CUSTOMER);

        order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.READY.name());
        order.setUser(customer);

        assignment = new DeliveryAssignment();
        assignment.setId(100L);
        assignment.setOrder(order);
        assignment.setShipper(shipper);
        assignment.setStatus(DeliveryAssignmentStatus.ASSIGNED.name());
    }

    @Test
    void shouldCreateUnassignedAssignment() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryAssignmentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        deliveryService.createUnassignedAssignment(orderId);

        verify(deliveryAssignmentRepository).save(any(DeliveryAssignment.class));
    }

    @Test
    void shouldNotCreateUnassignedAssignmentIfAlreadyExists() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryAssignmentRepository.findByOrderId(orderId)).thenReturn(Optional.of(assignment));

        deliveryService.createUnassignedAssignment(orderId);

        verify(deliveryAssignmentRepository, never()).save(any(DeliveryAssignment.class));
    }

    @Test
    void shouldAssignShipperSuccessfully() {
        AssignShipperRequest request = new AssignShipperRequest(orderId, shipperId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findById(shipperId)).thenReturn(Optional.of(shipper));
        when(deliveryAssignmentRepository.findByOrderId(orderId)).thenReturn(Optional.of(new DeliveryAssignment()));
        when(deliveryAssignmentRepository.save(any(DeliveryAssignment.class))).thenReturn(assignment);

        DeliveryAssignmentResponse response = deliveryService.assignShipper(request);

        assertNotNull(response);
        assertEquals(OrderStatus.SHIPPING.name(), order.getStatus());
        verify(orderRepository).save(order);
        verify(deliveryAssignmentRepository).save(any(DeliveryAssignment.class));
    }

    @Test
    void shouldThrowExceptionWhenAssigningNonShipperRole() {
        AssignShipperRequest request = new AssignShipperRequest(orderId, shipperId);
        shipper.setRole(Role.ROLE_CUSTOMER);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userRepository.findById(shipperId)).thenReturn(Optional.of(shipper));

        AppException exception = assertThrows(AppException.class, () -> deliveryService.assignShipper(request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("INVALID_ROLE", exception.getErrorCode());
    }

    @Test
    void shouldMarkOrderAsPickedUpSuccessfully() {
        when(deliveryAssignmentRepository.findByOrderId(orderId)).thenReturn(Optional.of(assignment));

        deliveryService.markPickedUp(shipperId, orderId, new MarkPickupRequest());

        assertEquals(DeliveryAssignmentStatus.PICKED_UP.name(), assignment.getStatus());
        assertNotNull(assignment.getPickedUpAt());
        verify(deliveryAssignmentRepository).save(assignment);
    }

    @Test
    void shouldThrowExceptionWhenMarkingPickedUpUnauthorized() {
        when(deliveryAssignmentRepository.findByOrderId(orderId)).thenReturn(Optional.of(assignment));

        MarkPickupRequest request = new MarkPickupRequest();
        AppException exception = assertThrows(AppException.class,
                () -> deliveryService.markPickedUp(999L, orderId, request));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void shouldMarkOrderAsDeliveredSuccessfully() {
        assignment.setStatus(DeliveryAssignmentStatus.PICKED_UP.name());
        when(deliveryAssignmentRepository.findByOrderId(orderId)).thenReturn(Optional.of(assignment));

        MarkDeliveredRequest request = new MarkDeliveredRequest();
        request.setCodCollected(true);
        deliveryService.markDelivered(shipperId, orderId, request);

        assertEquals(DeliveryAssignmentStatus.DELIVERED.name(), assignment.getStatus());
        assertEquals(OrderStatus.DELIVERED.name(), order.getStatus());
        assertTrue(order.getIsPaid());
        assertNotNull(assignment.getDeliveredAt());
        verify(deliveryAssignmentRepository).save(assignment);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowExceptionWhenMarkingDeliveredWithoutCod() {
        assignment.setStatus(DeliveryAssignmentStatus.PICKED_UP.name());
        when(deliveryAssignmentRepository.findByOrderId(orderId)).thenReturn(Optional.of(assignment));

        MarkDeliveredRequest request = new MarkDeliveredRequest();
        request.setCodCollected(false);
        
        AppException exception = assertThrows(AppException.class,
                () -> deliveryService.markDelivered(shipperId, orderId, request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("COD_NOT_COLLECTED", exception.getErrorCode());
    }

    @Test
    void shouldCreateNewLocationWhenUpdatingLocationForFirstTime() {
        ShipperLocationUpdateRequest request = new ShipperLocationUpdateRequest(new BigDecimal("10.0"),
                new BigDecimal("20.0"), true);
        when(shipperLocationRepository.findByShipperId(shipperId)).thenReturn(Optional.empty());
        when(userRepository.findById(shipperId)).thenReturn(Optional.of(shipper));

        deliveryService.updateLocation(shipperId, request);

        verify(shipperLocationRepository).save(any(ShipperLocation.class));
    }

    @Test
    void shouldUpdateExistingLocationWhenUpdatingLocation() {
        ShipperLocation existingLocation = new ShipperLocation();
        existingLocation.setShipper(shipper);
        ShipperLocationUpdateRequest request = new ShipperLocationUpdateRequest(new BigDecimal("11.0"),
                new BigDecimal("21.0"), false);

        when(shipperLocationRepository.findByShipperId(shipperId)).thenReturn(Optional.of(existingLocation));

        deliveryService.updateLocation(shipperId, request);

        assertEquals(new BigDecimal("11.0"), existingLocation.getLatitude());
        assertFalse(existingLocation.getIsOnline());
        verify(shipperLocationRepository).save(existingLocation);
    }

    @Test
    void shouldGetShipperLocationSuccessfullyForCustomer() {
        ShipperLocation loc = new ShipperLocation();
        loc.setShipper(shipper);
        loc.setLatitude(new BigDecimal("1.0"));
        loc.setLongitude(new BigDecimal("2.0"));

        when(shipperLocationRepository.findByShipperId(shipperId)).thenReturn(Optional.of(loc));
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(deliveryAssignmentRepository.findAllByShipperId(shipperId)).thenReturn(Collections.singletonList(assignment));

        ShipperLocationResponse response = deliveryService.getShipperLocation(shipperId, 2L);

        assertNotNull(response);
        assertEquals(new BigDecimal("1.0"), response.getLatitude());
    }

    @Test
    void shouldThrowExceptionWhenGettingShipperLocationUnauthorized() {
        ShipperLocation loc = new ShipperLocation();
        loc.setShipper(shipper);
        User stranger = new User();
        stranger.setId(99L);
        stranger.setRole(Role.ROLE_CUSTOMER);

        when(shipperLocationRepository.findByShipperId(shipperId)).thenReturn(Optional.of(loc));
        when(userRepository.findById(99L)).thenReturn(Optional.of(stranger));
        when(deliveryAssignmentRepository.findAllByShipperId(shipperId)).thenReturn(Collections.emptyList());

        assertThrows(AppException.class, () -> deliveryService.getShipperLocation(shipperId, 99L));
    }

    @Test
    void shouldGetAvailableDeliveries() {
        when(deliveryAssignmentRepository.findAllByShipperIsNullAndStatus("UNASSIGNED"))
                .thenReturn(Collections.singletonList(new DeliveryAssignment()));

        List<DeliveryAssignmentResponse> result = deliveryService.getAvailableDeliveries();

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetMyDeliveries() {
        when(deliveryAssignmentRepository.findAllByShipperId(shipperId))
                .thenReturn(Collections.singletonList(assignment));

        List<DeliveryAssignmentResponse> result = deliveryService.getMyDeliveries(shipperId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetByOrderId() {
        when(deliveryAssignmentRepository.findByOrderId(orderId)).thenReturn(Optional.of(assignment));
        when(userRepository.findById(shipperId)).thenReturn(Optional.of(shipper));

        DeliveryAssignmentResponse response = deliveryService.getByOrderId(orderId, shipperId);

        assertNotNull(response);
    }
}

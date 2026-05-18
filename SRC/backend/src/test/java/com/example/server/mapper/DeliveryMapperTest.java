package com.example.server.mapper;

import com.example.server.dto.delivery.DeliveryAssignmentResponse;
import com.example.server.dto.delivery.ShipperLocationResponse;
import com.example.server.entity.DeliveryAssignment;
import com.example.server.entity.Order;
import com.example.server.entity.ShipperLocation;
import com.example.server.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryMapperTest {

    private final DeliveryMapper mapper = Mappers.getMapper(DeliveryMapper.class);

    @Test
    void shouldMapToResponse() {
        Order order = new Order();
        order.setId(10L);

        User shipper = new User();
        shipper.setId(20L);
        shipper.setFullName("John Doe");

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setId(1L);
        assignment.setOrder(order);
        assignment.setShipper(shipper);
        assignment.setStatus("ASSIGNED");
        assignment.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));

        DeliveryAssignmentResponse response = mapper.toResponse(assignment);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getOrderId());
        assertEquals(20L, response.getShipperId());
        assertEquals("John Doe", response.getShipperName());
        assertEquals("ASSIGNED", response.getStatus());
    }

    @Test
    void shouldMapToResponseWithNullShipper() {
        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setId(1L);

        DeliveryAssignmentResponse response = mapper.toResponse(assignment);

        assertNotNull(response);
        assertNull(response.getShipperId());
        assertNull(response.getShipperName());
    }
    
    @Test
    void shouldMapToResponseWithNulls() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void shouldMapToLocationResponse() {
        User shipper = new User();
        shipper.setId(20L);

        ShipperLocation location = new ShipperLocation();
        location.setId(1L);
        location.setShipper(shipper);
        location.setLatitude(new BigDecimal("10.0"));
        location.setLongitude(new BigDecimal("20.0"));
        location.setIsOnline(true);

        ShipperLocationResponse response = mapper.toLocationResponse(location);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(20L, response.getShipperId());
        assertEquals(new BigDecimal("10.0"), response.getLatitude());
        assertTrue(response.getIsOnline());
    }

    @Test
    void shouldMapToLocationResponseWithNulls() {
        assertNull(mapper.toLocationResponse(null));
    }
}

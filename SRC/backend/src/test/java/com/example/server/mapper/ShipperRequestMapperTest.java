package com.example.server.mapper;

import com.example.server.dto.shipper.ShipperRequestResponse;
import com.example.server.entity.ShipperRequest;
import com.example.server.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ShipperRequestMapperTest {

    private final ShipperRequestMapper mapper = Mappers.getMapper(ShipperRequestMapper.class);

    @Test
    void shouldMapToResponse() {
        User user = new User();
        user.setId(10L);
        user.setEmail("shipper@test.com");

        ShipperRequest request = new ShipperRequest();
        request.setId(1L);
        request.setUser(user);
        request.setLicensePlate("ABC-123");

        ShipperRequestResponse response = mapper.toResponse(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getUserId());
        assertEquals("shipper@test.com", response.getUserEmail());
        assertEquals("ABC-123", response.getLicensePlate());
    }

    @Test
    void shouldMapToResponseWithNullUser() {
        ShipperRequest request = new ShipperRequest();
        request.setId(1L);

        ShipperRequestResponse response = mapper.toResponse(request);

        assertNotNull(response);
        assertNull(response.getUserId());
        assertNull(response.getUserEmail());
    }

    @Test
    void shouldMapToResponseWithNulls() {
        assertNull(mapper.toResponse(null));
    }
}

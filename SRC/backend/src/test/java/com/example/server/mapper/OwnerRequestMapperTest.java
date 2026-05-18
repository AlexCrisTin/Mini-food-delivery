package com.example.server.mapper;

import com.example.server.dto.owner.OwnerRequestResponse;
import com.example.server.entity.OwnerRequest;
import com.example.server.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class OwnerRequestMapperTest {

    private final OwnerRequestMapper mapper = Mappers.getMapper(OwnerRequestMapper.class);

    @Test
    void shouldMapToResponse() {
        User user = new User();
        user.setId(10L);
        user.setEmail("owner@test.com");

        OwnerRequest request = new OwnerRequest();
        request.setId(1L);
        request.setUser(user);
        request.setRestaurantName("New Restaurant");

        OwnerRequestResponse response = mapper.toResponse(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getUserId());
        assertEquals("owner@test.com", response.getUserEmail());
        assertEquals("New Restaurant", response.getRestaurantName());
    }

    @Test
    void shouldMapToResponseWithNullUser() {
        OwnerRequest request = new OwnerRequest();
        request.setId(1L);

        OwnerRequestResponse response = mapper.toResponse(request);

        assertNotNull(response);
        assertNull(response.getUserId());
        assertNull(response.getUserEmail());
    }

    @Test
    void shouldMapToResponseWithNulls() {
        assertNull(mapper.toResponse(null));
    }
}

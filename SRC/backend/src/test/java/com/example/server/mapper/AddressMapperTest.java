package com.example.server.mapper;

import com.example.server.dto.user.AddressRequest;
import com.example.server.dto.user.AddressResponse;
import com.example.server.entity.Address;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AddressMapperTest {

    private final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

    @Test
    void shouldMapToResponse() {
        Address address = new Address();
        address.setId(1L);
        address.setLabel("Home");
        address.setAddressLine("123 Test St");
        address.setLatitude(new BigDecimal("10.1"));
        address.setLongitude(new BigDecimal("20.2"));
        address.setIsDefault(true);

        AddressResponse response = mapper.toResponse(address);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Home", response.getLabel());
        assertEquals("123 Test St", response.getAddressLine());
        assertEquals(new BigDecimal("10.1"), response.getLatitude());
        assertEquals(new BigDecimal("20.2"), response.getLongitude());
        assertTrue(response.getIsDefault());
    }

    @Test
    void shouldMapToResponseWithNulls() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void shouldMapToEntity() {
        AddressRequest request = new AddressRequest();
        request.setLabel("Work");
        request.setAddressLine("456 Work Ave");
        request.setLatitude(new BigDecimal("30.3"));
        request.setLongitude(new BigDecimal("40.4"));
        request.setIsDefault(false);

        Address address = mapper.toEntity(request);

        assertNotNull(address);
        assertNull(address.getId());
        assertNull(address.getUser());
        assertEquals("Work", address.getLabel());
        assertEquals("456 Work Ave", address.getAddressLine());
        assertEquals(new BigDecimal("30.3"), address.getLatitude());
        assertEquals(new BigDecimal("40.4"), address.getLongitude());
        assertFalse(address.getIsDefault());
    }

    @Test
    void shouldMapToEntityWithNulls() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void shouldUpdateEntity() {
        Address address = new Address();
        address.setId(1L);
        address.setLabel("Old Label");
        
        AddressRequest request = new AddressRequest();
        request.setLabel("New Label");
        request.setIsDefault(true);

        mapper.updateEntity(address, request);

        assertEquals(1L, address.getId());
        assertEquals("New Label", address.getLabel());
        assertTrue(address.getIsDefault());
    }
    
    @Test
    void shouldNotUpdateEntityIfRequestIsNull() {
        Address address = new Address();
        address.setLabel("Old Label");
        
        mapper.updateEntity(address, null);
        
        assertEquals("Old Label", address.getLabel());
    }
}

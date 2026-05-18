package com.example.server.mapper;

import com.example.server.dto.user.UserProfileResponse;
import com.example.server.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(mapper, "addressMapper", Mappers.getMapper(AddressMapper.class));
    }

    @Test
    void shouldMapToProfileResponse() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setFullName("Test User");

        UserProfileResponse response = mapper.toProfileResponse(user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("user@test.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
    }

    @Test
    void shouldMapToProfileResponseWithNulls() {
        assertNull(mapper.toProfileResponse(null));
    }
}

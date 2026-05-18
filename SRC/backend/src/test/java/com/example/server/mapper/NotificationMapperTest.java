package com.example.server.mapper;

import com.example.server.dto.notification.NotificationResponse;
import com.example.server.entity.Notification;
import com.example.server.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {

    private final NotificationMapper mapper = Mappers.getMapper(NotificationMapper.class);

    @Test
    void shouldMapToResponse() {
        User user = new User();
        user.setId(10L);

        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setTitle("Alert");
        notification.setMessage("Test message");

        NotificationResponse response = mapper.toResponse(notification);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getUserId());
        assertEquals("Alert", response.getTitle());
        assertEquals("Test message", response.getMessage());
    }

    @Test
    void shouldMapToResponseWithNullUser() {
        Notification notification = new Notification();
        notification.setId(1L);

        NotificationResponse response = mapper.toResponse(notification);

        assertNotNull(response);
        assertNull(response.getUserId());
    }

    @Test
    void shouldMapToResponseWithNulls() {
        assertNull(mapper.toResponse(null));
    }
}

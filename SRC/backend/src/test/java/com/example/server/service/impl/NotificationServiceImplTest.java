package com.example.server.service.impl;

import com.example.server.dto.notification.MarkAllNotificationsReadRequest;
import com.example.server.dto.notification.MarkNotificationReadRequest;
import com.example.server.dto.notification.NotificationResponse;
import com.example.server.entity.Notification;
import com.example.server.entity.User;
import com.example.server.exception.AppException;
import com.example.server.exception.ResourceNotFoundException;
import com.example.server.mapper.NotificationMapper;
import com.example.server.repository.NotificationRepository;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationMapper notificationMapper;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;
    private final Long userId = 1L;
    private final Long notificationId = 100L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        notification = new Notification();
        notification.setId(notificationId);
        notification.setUser(user);
        notification.setTitle("Test Title");
        notification.setMessage("Test Message");
        notification.setType("INFO");
        notification.setIsRead(false);
    }

    @Test
    void shouldReturnMappedNotificationsForUser() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Arrays.asList(notification));
        
        NotificationResponse response = NotificationResponse.builder()
                .id(notificationId)
                .title("Test Title")
                .isRead(false)
                .build();
        
        when(notificationMapper.toResponse(notification)).thenReturn(response);

        List<NotificationResponse> result = notificationService.getUserNotifications(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(notificationId, result.get(0).getId());
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void shouldMarkNotificationAsReadSuccessfully() {
        MarkNotificationReadRequest request = new MarkNotificationReadRequest(notificationId);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(userId, request);

        assertTrue(notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    void shouldThrowExceptionWhenMarkingNonExistentNotificationAsRead() {
        MarkNotificationReadRequest request = new MarkNotificationReadRequest(notificationId);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(userId, request));
    }

    @Test
    void shouldThrowExceptionWhenMarkingAsReadUnauthorized() {
        MarkNotificationReadRequest request = new MarkNotificationReadRequest(notificationId);
        User otherUser = new User();
        otherUser.setId(2L);
        notification.setUser(otherUser);
        
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        AppException exception = assertThrows(AppException.class, () -> notificationService.markAsRead(userId, request));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("UNAUTHORIZED_NOTIFICATION_ACCESS", exception.getErrorCode());
    }

    @Test
    void shouldMarkAllNotificationsAsReadWhenNoTypeProvided() {
        MarkAllNotificationsReadRequest request = new MarkAllNotificationsReadRequest(null);

        notificationService.markAllAsRead(userId, request);

        verify(notificationRepository).markAllAsRead(userId);
        verify(notificationRepository, never()).markAllByTypeAsRead(anyLong(), anyString());
    }

    @Test
    void shouldMarkAllNotificationsOfTypeAsReadWhenTypeProvided() {
        String type = "ORDER";
        MarkAllNotificationsReadRequest request = new MarkAllNotificationsReadRequest(type);

        notificationService.markAllAsRead(userId, request);

        verify(notificationRepository).markAllByTypeAsRead(userId, type);
        verify(notificationRepository, never()).markAllAsRead(userId);
    }

    @Test
    void shouldCreateNotificationSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        notificationService.createNotification(userId, "Title", "Message", "INFO");

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingNotificationForNonExistentUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> notificationService.createNotification(userId, "Title", "Message", "INFO"));
    }
}

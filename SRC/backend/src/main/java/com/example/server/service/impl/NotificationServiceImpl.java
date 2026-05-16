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
import com.example.server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final String RESOURCE_NOTIFICATION = "Notification";
    private static final String RESOURCE_USER = "User";

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, MarkNotificationReadRequest request) {
        Notification notification = notificationRepository.findById(request.getNotificationId())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOTIFICATION, "id", request.getNotificationId()));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "Unauthorized to mark this notification as read", "UNAUTHORIZED_NOTIFICATION_ACCESS");
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId, MarkAllNotificationsReadRequest request) {
        if (request.getType() != null && !request.getType().isBlank()) {
            notificationRepository.markAllByTypeAsRead(userId, request.getType());
        } else {
            notificationRepository.markAllAsRead(userId);
        }
    }

    @Override
    @Transactional
    public void createNotification(Long userId, String title, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_USER, "id", userId));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Broadcast to user via WebSocket
        try {
            NotificationResponse response = notificationMapper.toResponse(savedNotification);
            log.info("Broadcasting notification to user {}: {}", user.getEmail(), title);
            messagingTemplate.convertAndSendToUser(
                    user.getEmail(), 
                    "/queue/notifications", 
                    response
            );
        } catch (Exception e) {
            log.error("Failed to broadcast notification: {}", e.getMessage());
        }
    }
}

package com.example.server.listener;

import com.example.server.event.OrderReadyEvent;
import com.example.server.service.DeliveryService;
import com.example.server.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private DeliveryService deliveryService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Test
    void shouldCreateUnassignedAssignmentSuccessfully() {
        Long orderId = 100L;
        OrderReadyEvent event = new OrderReadyEvent(this, orderId);

        orderEventListener.handleOrderReadyEvent(event);

        verify(deliveryService).createUnassignedAssignment(orderId);
        verify(notificationService, never()).createNotification(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void shouldNotifyAdminWhenAssignmentFails() {
        Long orderId = 100L;
        OrderReadyEvent event = new OrderReadyEvent(this, orderId);

        doThrow(new RuntimeException("DB Error")).when(deliveryService).createUnassignedAssignment(orderId);

        orderEventListener.handleOrderReadyEvent(event);

        verify(notificationService).createNotification(
                eq(1L),
                eq("Delivery Assignment Failed"),
                contains("order #100"),
                eq("SYSTEM_ERROR")
        );
    }

    @Test
    void shouldLogWhenNotificationAlsoFails() {
        Long orderId = 100L;
        OrderReadyEvent event = new OrderReadyEvent(this, orderId);

        doThrow(new RuntimeException("Delivery Error")).when(deliveryService).createUnassignedAssignment(orderId);
        doThrow(new RuntimeException("Notification Error")).when(notificationService)
                .createNotification(anyLong(), anyString(), anyString(), anyString());

        // Should not throw exception, just log it
        orderEventListener.handleOrderReadyEvent(event);

        verify(deliveryService).createUnassignedAssignment(orderId);
        verify(notificationService).createNotification(anyLong(), anyString(), anyString(), anyString());
    }
}

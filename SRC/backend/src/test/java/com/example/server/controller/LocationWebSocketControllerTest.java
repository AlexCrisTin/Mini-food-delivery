package com.example.server.controller;

import com.example.server.dto.delivery.ShipperLocationDTO;
import com.example.server.entity.ShipperLocation;
import com.example.server.entity.User;
import com.example.server.repository.ShipperLocationRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationWebSocketControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ShipperLocationRepository shipperLocationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LocationWebSocketController controller;

    private CustomUserDetails shipperDetails;
    private Principal principal;
    private ShipperLocationDTO locationDTO;

    @BeforeEach
    void setUp() {
        shipperDetails = CustomUserDetails.builder()
                .id(1L)
                .email("shipper@test.com")
                .authorities(Collections.emptyList())
                .build();

        principal = new UsernamePasswordAuthenticationToken(shipperDetails, null);

        locationDTO = ShipperLocationDTO.builder()
                .orderId(100L)
                .shipperId(1L)
                .latitude(new BigDecimal("10.0"))
                .longitude(new BigDecimal("10.0"))
                .build();
    }

    @Test
    void shouldHandleShipperLocationSuccessfully() {
        when(shipperLocationRepository.findByShipperId(1L)).thenReturn(Optional.empty());
        User shipper = new User();
        shipper.setId(1L);
        when(userRepository.getReferenceById(1L)).thenReturn(shipper);

        controller.handleShipperLocation(locationDTO, principal);

        verify(shipperLocationRepository).save(any(ShipperLocation.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/order/100"), eq(locationDTO));
    }

    @Test
    void shouldUpdateExistingShipperLocationSuccessfully() {
        ShipperLocation existingLoc = new ShipperLocation();
        existingLoc.setShipper(new User());
        when(shipperLocationRepository.findByShipperId(1L)).thenReturn(Optional.of(existingLoc));

        controller.handleShipperLocation(locationDTO, principal);

        verify(shipperLocationRepository).save(existingLoc);
        verify(messagingTemplate).convertAndSend(anyString(), any(ShipperLocationDTO.class));
    }

    @Test
    void shouldIgnoreWhenPrincipalIsNull() {
        controller.handleShipperLocation(locationDTO, null);

        verify(shipperLocationRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(ShipperLocationDTO.class));
    }

    @Test
    void shouldIgnoreWhenShipperIdMismatch() {
        locationDTO.setShipperId(2L); // Different from authenticated user id 1L

        controller.handleShipperLocation(locationDTO, principal);

        verify(shipperLocationRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(ShipperLocationDTO.class));
    }
}

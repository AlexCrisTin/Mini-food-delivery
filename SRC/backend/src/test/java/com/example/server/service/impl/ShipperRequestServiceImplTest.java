package com.example.server.service.impl;

import com.example.server.dto.shipper.ShipperRequestApproval;
import com.example.server.dto.shipper.ShipperRequestResponse;
import com.example.server.dto.shipper.ShipperRequestSubmission;
import com.example.server.entity.ShipperLocation;
import com.example.server.entity.ShipperRequest;
import com.example.server.entity.User;
import com.example.server.enums.Role;
import com.example.server.enums.ShipperRequestStatus;
import com.example.server.exception.AppException;
import com.example.server.exception.ResourceNotFoundException;
import com.example.server.repository.ShipperLocationRepository;
import com.example.server.repository.ShipperRequestRepository;
import com.example.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipperRequestServiceImplTest {

    @Mock
    private ShipperRequestRepository shipperRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShipperLocationRepository shipperLocationRepository;

    @InjectMocks
    private ShipperRequestServiceImpl shipperRequestService;

    private User user;
    private ShipperRequest shipperRequest;
    private Long userId = 1L;
    private Long requestId = 2L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setEmail("shipper@example.com");

        shipperRequest = ShipperRequest.builder()
                .id(requestId)
                .user(user)
                .phoneNumber("0123456789")
                .licensePlate("ABC-123")
                .status(ShipperRequestStatus.PENDING)
                .build();
    }

    @Test
    void shouldSubmitRequestSuccessfully() {
        ShipperRequestSubmission submission = new ShipperRequestSubmission();
        submission.setPhoneNumber("0123456789");
        submission.setLicensePlate("ABC-123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shipperRequestRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(shipperRequestRepository.save(any(ShipperRequest.class))).thenReturn(shipperRequest);

        var response = shipperRequestService.submitRequest(userId, submission);

        assertNotNull(response);
        assertEquals(ShipperRequestStatus.PENDING, response.getStatus());
        verify(shipperRequestRepository).save(any(ShipperRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenPendingRequestExists() {
        ShipperRequestSubmission submission = new ShipperRequestSubmission();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shipperRequestRepository.findByUserId(userId)).thenReturn(Collections.singletonList(shipperRequest));

        assertThrows(AppException.class, () -> shipperRequestService.submitRequest(userId, submission));
    }

    @Test
    void shouldProcessApprovalSuccessfully() {
        ShipperRequestApproval approval = new ShipperRequestApproval(true, "Approved!");
        when(shipperRequestRepository.findById(requestId)).thenReturn(Optional.of(shipperRequest));
        when(shipperRequestRepository.save(any(ShipperRequest.class))).thenReturn(shipperRequest);
        when(shipperLocationRepository.findByShipperId(userId)).thenReturn(Optional.empty());

        var response = shipperRequestService.processRequest(requestId, approval);

        assertEquals(ShipperRequestStatus.APPROVED, response.getStatus());
        assertEquals(Role.ROLE_SHIPPER, user.getRole());
        verify(shipperLocationRepository).save(any(ShipperLocation.class));
        verify(userRepository).save(user);
    }

    @Test
    void shouldProcessRejectionSuccessfully() {
        ShipperRequestApproval approval = new ShipperRequestApproval(false, "Rejected!");
        when(shipperRequestRepository.findById(requestId)).thenReturn(Optional.of(shipperRequest));
        when(shipperRequestRepository.save(any(ShipperRequest.class))).thenReturn(shipperRequest);

        var response = shipperRequestService.processRequest(requestId, approval);

        assertEquals(ShipperRequestStatus.REJECTED, response.getStatus());
        assertNotEquals(Role.ROLE_SHIPPER, user.getRole());
        verify(shipperLocationRepository, never()).save(any(ShipperLocation.class));
    }

    @Test
    void shouldThrowExceptionWhenProcessingAlreadyProcessedRequest() {
        shipperRequest.setStatus(ShipperRequestStatus.APPROVED);
        ShipperRequestApproval approval = new ShipperRequestApproval(true, "OK");
        when(shipperRequestRepository.findById(requestId)).thenReturn(Optional.of(shipperRequest));

        assertThrows(AppException.class, () -> shipperRequestService.processRequest(requestId, approval));
    }
}

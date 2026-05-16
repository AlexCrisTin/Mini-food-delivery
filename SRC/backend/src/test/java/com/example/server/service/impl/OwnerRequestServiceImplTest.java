package com.example.server.service.impl;

import com.example.server.dto.owner.OwnerRequestApproval;
import com.example.server.dto.owner.OwnerRequestResponse;
import com.example.server.dto.owner.OwnerRequestSubmission;
import com.example.server.entity.OwnerRequest;
import com.example.server.entity.Restaurant;
import com.example.server.entity.User;
import com.example.server.enums.OwnerRequestStatus;
import com.example.server.enums.Role;
import com.example.server.exception.AppException;
import com.example.server.exception.ResourceNotFoundException;
import com.example.server.repository.OwnerRequestRepository;
import com.example.server.repository.RestaurantRepository;
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
class OwnerRequestServiceImplTest {

    @Mock
    private OwnerRequestRepository ownerRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private OwnerRequestServiceImpl ownerRequestService;

    private User user;
    private OwnerRequest ownerRequest;
    private Long userId = 1L;
    private Long requestId = 2L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");

        ownerRequest = OwnerRequest.builder()
                .id(requestId)
                .user(user)
                .restaurantName("New Restaurant")
                .status(OwnerRequestStatus.PENDING)
                .build();
    }

    @Test
    void shouldSubmitRequestSuccessfully() {
        OwnerRequestSubmission submission = new OwnerRequestSubmission();
        submission.setRestaurantName("New Restaurant");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ownerRequestRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(ownerRequestRepository.save(any(OwnerRequest.class))).thenReturn(ownerRequest);

        var response = ownerRequestService.submitRequest(userId, submission);

        assertNotNull(response);
        assertEquals(OwnerRequestStatus.PENDING, response.getStatus());
        verify(ownerRequestRepository).save(any(OwnerRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenPendingRequestExists() {
        OwnerRequestSubmission submission = new OwnerRequestSubmission();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ownerRequestRepository.findByUserId(userId)).thenReturn(Collections.singletonList(ownerRequest));

        assertThrows(AppException.class, () -> ownerRequestService.submitRequest(userId, submission));
    }

    @Test
    void shouldProcessApprovalSuccessfully() {
        OwnerRequestApproval approval = new OwnerRequestApproval(true, "Approved!");
        when(ownerRequestRepository.findById(requestId)).thenReturn(Optional.of(ownerRequest));
        when(ownerRequestRepository.save(any(OwnerRequest.class))).thenReturn(ownerRequest);

        var response = ownerRequestService.processRequest(requestId, approval);

        assertEquals(OwnerRequestStatus.APPROVED, response.getStatus());
        assertEquals(Role.ROLE_OWNER, user.getRole());
        verify(restaurantRepository).save(any(Restaurant.class));
        verify(userRepository).save(user);
    }

    @Test
    void shouldProcessRejectionSuccessfully() {
        OwnerRequestApproval approval = new OwnerRequestApproval(false, "Rejected!");
        when(ownerRequestRepository.findById(requestId)).thenReturn(Optional.of(ownerRequest));
        when(ownerRequestRepository.save(any(OwnerRequest.class))).thenReturn(ownerRequest);

        var response = ownerRequestService.processRequest(requestId, approval);

        assertEquals(OwnerRequestStatus.REJECTED, response.getStatus());
        assertNotEquals(Role.ROLE_OWNER, user.getRole());
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void shouldThrowExceptionWhenProcessingAlreadyProcessedRequest() {
        ownerRequest.setStatus(OwnerRequestStatus.APPROVED);
        OwnerRequestApproval approval = new OwnerRequestApproval(true, "OK");
        when(ownerRequestRepository.findById(requestId)).thenReturn(Optional.of(ownerRequest));

        assertThrows(AppException.class, () -> ownerRequestService.processRequest(requestId, approval));
    }
}

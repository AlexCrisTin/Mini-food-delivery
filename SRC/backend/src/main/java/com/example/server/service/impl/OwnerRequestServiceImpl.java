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
import com.example.server.mapper.OwnerRequestMapper;
import com.example.server.repository.OwnerRequestRepository;
import com.example.server.repository.RestaurantRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.OwnerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerRequestServiceImpl implements OwnerRequestService {

    private final OwnerRequestRepository ownerRequestRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OwnerRequestMapper ownerRequestMapper;

    @Override
    @Transactional
    public OwnerRequestResponse submitRequest(Long userId, OwnerRequestSubmission request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Optional: check if user already has a pending request
        List<OwnerRequest> pendingRequests = ownerRequestRepository.findByUserId(userId).stream()
                .filter(r -> r.getStatus() == OwnerRequestStatus.PENDING)
                .collect(Collectors.toList());
        
        if (!pendingRequests.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "You already have a pending owner request", "PENDING_REQUEST_EXISTS");
        }

        OwnerRequest ownerRequest = OwnerRequest.builder()
                .user(user)
                .restaurantName(request.getRestaurantName())
                .restaurantAddress(request.getRestaurantAddress())
                .restaurantPhone(request.getRestaurantPhone())
                .description(request.getDescription())
                .status(OwnerRequestStatus.PENDING)
                .build();

        return ownerRequestMapper.toResponse(ownerRequestRepository.save(ownerRequest));
    }

    @Override
    public List<OwnerRequestResponse> getUserRequests(Long userId) {
        return ownerRequestRepository.findByUserId(userId).stream()
                .map(ownerRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OwnerRequestResponse> getAllPendingRequests() {
        return ownerRequestRepository.findByStatus(OwnerRequestStatus.PENDING).stream()
                .map(ownerRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OwnerRequestResponse processRequest(Long requestId, OwnerRequestApproval approval) {
        OwnerRequest request = ownerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("OwnerRequest", "id", requestId));

        if (request.getStatus() != OwnerRequestStatus.PENDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Request is already processed", "REQUEST_ALREADY_PROCESSED");
        }

        if (approval.isApproved()) {
            request.setStatus(OwnerRequestStatus.APPROVED);
            promoteToOwner(request);
        } else {
            request.setStatus(OwnerRequestStatus.REJECTED);
        }

        request.setAdminNote(approval.getAdminNote());
        return ownerRequestMapper.toResponse(ownerRequestRepository.save(request));
    }

    private void promoteToOwner(OwnerRequest request) {
        User user = request.getUser();
        
        // 1. Update user role
        user.setRole(Role.ROLE_OWNER);
        userRepository.save(user);

        // 2. Create restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setOwner(user);
        restaurant.setName(request.getRestaurantName());
        restaurant.setAddress(request.getRestaurantAddress());
        restaurant.setPhone(request.getRestaurantPhone());
        restaurant.setDescription(request.getDescription());
        restaurant.setOpeningTime(LocalTime.of(8, 0)); // Default values
        restaurant.setClosingTime(LocalTime.of(22, 0));
        restaurant.setIsApproved(true);
        restaurant.setIsDeleted(false);
        restaurant.setIsOpen(true);
        
        restaurantRepository.save(restaurant);
    }
}

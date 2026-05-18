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
import com.example.server.mapper.ShipperRequestMapper;
import com.example.server.repository.ShipperLocationRepository;
import com.example.server.repository.ShipperRequestRepository;
import com.example.server.repository.UserRepository;
import com.example.server.service.ShipperRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipperRequestServiceImpl implements ShipperRequestService {

    private final ShipperRequestRepository shipperRequestRepository;
    private final UserRepository userRepository;
    private final ShipperLocationRepository shipperLocationRepository;
    private final ShipperRequestMapper shipperRequestMapper;

    @Override
    @Transactional
    public ShipperRequestResponse submitRequest(Long userId, ShipperRequestSubmission request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if user already has a pending request
        List<ShipperRequest> pendingRequests = shipperRequestRepository.findByUserId(userId).stream()
                .filter(r -> r.getStatus() == ShipperRequestStatus.PENDING)
                .collect(Collectors.toList());
        
        if (!pendingRequests.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "You already have a pending shipper request", "PENDING_REQUEST_EXISTS");
        }

        ShipperRequest shipperRequest = ShipperRequest.builder()
                .user(user)
                .phoneNumber(request.getPhoneNumber())
                .licensePlate(request.getLicensePlate())
                .status(ShipperRequestStatus.PENDING)
                .build();

        return shipperRequestMapper.toResponse(shipperRequestRepository.save(shipperRequest));
    }

    @Override
    public List<ShipperRequestResponse> getUserRequests(Long userId) {
        return shipperRequestRepository.findByUserId(userId).stream()
                .map(shipperRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShipperRequestResponse> getAllPendingRequests() {
        return shipperRequestRepository.findByStatus(ShipperRequestStatus.PENDING).stream()
                .map(shipperRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShipperRequestResponse processRequest(Long requestId, ShipperRequestApproval approval) {
        ShipperRequest request = shipperRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("ShipperRequest", "id", requestId));

        if (request.getStatus() != ShipperRequestStatus.PENDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Request is already processed", "REQUEST_ALREADY_PROCESSED");
        }

        if (approval.isApproved()) {
            request.setStatus(ShipperRequestStatus.APPROVED);
            promoteToShipper(request);
        } else {
            request.setStatus(ShipperRequestStatus.REJECTED);
        }

        request.setAdminNote(approval.getAdminNote());
        return shipperRequestMapper.toResponse(shipperRequestRepository.save(request));
    }

    private void promoteToShipper(ShipperRequest request) {
        User user = request.getUser();
        
        // 1. Update user role
        user.setRole(Role.ROLE_SHIPPER); // Using Role.ROLE_SHIPPER String constant
        userRepository.save(user);

        // 2. Create initial offline location
        if (shipperLocationRepository.findByShipperId(user.getId()).isEmpty()) {
            ShipperLocation location = new ShipperLocation();
            location.setShipper(user);
            location.setLatitude(new BigDecimal("10.762622")); // Default HCM City coords or similar
            location.setLongitude(new BigDecimal("106.660172"));
            location.setIsOnline(false);
            shipperLocationRepository.save(location);
        }
    }
}

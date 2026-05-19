package com.example.server.mapper;

import com.example.server.dto.shipper.ShipperRequestResponse;
import com.example.server.entity.ShipperRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipperRequestMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    ShipperRequestResponse toResponse(ShipperRequest request);
}

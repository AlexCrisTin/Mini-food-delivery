package com.example.server.mapper;

import com.example.server.dto.owner.OwnerRequestResponse;
import com.example.server.entity.OwnerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OwnerRequestMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    OwnerRequestResponse toResponse(OwnerRequest request);
}

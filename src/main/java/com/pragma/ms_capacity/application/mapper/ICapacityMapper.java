package com.pragma.ms_capacity.application.mapper;

import com.pragma.ms_capacity.application.dto.CapacityRequest;
import com.pragma.ms_capacity.application.dto.CapacityResponse;
import com.pragma.ms_capacity.application.dto.TechnologyIdRequest;
import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.model.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ICapacityMapper {
    Capacity toDomain(CapacityRequest request);

    CapacityResponse toResponse(Capacity capacity);

    default List<Technology> mapTechnologyRequests(List<TechnologyIdRequest> requests) {
        if (requests == null) return List.of();
        return requests.stream()
                .map(req -> new Technology(req.getId(), null))
                .toList();
    }
}

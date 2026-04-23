package com.pragma.ms_capacity.application.handler.impl;

import com.pragma.ms_capacity.application.dto.CapacityRequest;
import com.pragma.ms_capacity.application.dto.CapacityResponse;
import com.pragma.ms_capacity.application.dto.PagedResponse;
import com.pragma.ms_capacity.application.handler.ICapacityHandler;
import com.pragma.ms_capacity.application.mapper.ICapacityMapper;
import com.pragma.ms_capacity.domain.api.ICapacityServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CapacityHandlerImpl implements ICapacityHandler {

    private final ICapacityServicePort capacityServicePort;
    private final ICapacityMapper capacityMapper;

    @Override
    public Mono<CapacityResponse> save(CapacityRequest request) {
        return capacityServicePort.save(capacityMapper.toDomain(request))
                .map(capacityMapper::toResponse);
    }

    @Override
    public Mono<PagedResponse<CapacityResponse>> findAll(int page, int size, String sortBy, boolean ascending) {
        return capacityServicePort.findAll(page, size, sortBy, ascending)
                .map(paged -> new PagedResponse<>(
                        paged.getContent().stream()
                                .map(capacityMapper::toResponse)
                                .toList(),
                        paged.getPage(),
                        paged.getSize(),
                        paged.getTotalElements(),
                        paged.getTotalPages()
                ));
    }
}
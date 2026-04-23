package com.pragma.ms_capacity.application.handler;

import com.pragma.ms_capacity.application.dto.CapacityRequest;
import com.pragma.ms_capacity.application.dto.CapacityResponse;
import com.pragma.ms_capacity.application.dto.PagedResponse;
import reactor.core.publisher.Mono;

public interface ICapacityHandler {
    Mono<CapacityResponse> save(CapacityRequest request);

    Mono<CapacityResponse> findById(Long id);

    Mono<PagedResponse<CapacityResponse>> findAll(int page, int size, String sortBy, boolean ascending);
}

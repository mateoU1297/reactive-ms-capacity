package com.pragma.ms_capacity.application.handler;

import com.pragma.ms_capacity.application.dto.CapacityRequest;
import com.pragma.ms_capacity.application.dto.CapacityResponse;
import reactor.core.publisher.Mono;

public interface ICapacityHandler {
    Mono<CapacityResponse> save(CapacityRequest request);
}

package com.pragma.ms_capacity.domain.api;

import com.pragma.ms_capacity.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityServicePort {
    Mono<Capacity> save(Capacity capacity);
}
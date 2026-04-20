package com.pragma.ms_capacity.domain.spi;

import com.pragma.ms_capacity.domain.model.Capacity;
import reactor.core.publisher.Mono;

public interface ICapacityPersistencePort {
    Mono<Capacity> save(Capacity capacity);

    Mono<Boolean> existsByName(String name);
}

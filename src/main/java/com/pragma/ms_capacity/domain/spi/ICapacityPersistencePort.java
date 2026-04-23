package com.pragma.ms_capacity.domain.spi;

import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.model.PagedResult;
import reactor.core.publisher.Mono;

public interface ICapacityPersistencePort {
    Mono<Capacity> save(Capacity capacity);

    Mono<Capacity> findById(Long id);

    Mono<Boolean> existsByName(String name);

    Mono<PagedResult<Capacity>> findAll(int page, int size, String sortBy, boolean ascending);
}

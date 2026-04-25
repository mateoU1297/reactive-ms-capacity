package com.pragma.ms_capacity.domain.api;

import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.model.PagedResult;
import reactor.core.publisher.Mono;

public interface ICapacityServicePort {
    Mono<Capacity> save(Capacity capacity);

    Mono<Capacity> findById(Long id);

    Mono<PagedResult<Capacity>> findAll(int page, int size, String sortBy, boolean ascending);

    Mono<Void> delete(Long id);
}
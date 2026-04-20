package com.pragma.ms_capacity.infrastructure.out.repository;

import com.pragma.ms_capacity.infrastructure.out.entity.CapacityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CapacityRepository extends ReactiveCrudRepository<CapacityEntity, Long> {
    Mono<Boolean> existsByName(String name);
}

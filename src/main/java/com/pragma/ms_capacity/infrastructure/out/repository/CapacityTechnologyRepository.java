package com.pragma.ms_capacity.infrastructure.out.repository;

import com.pragma.ms_capacity.infrastructure.out.entity.CapacityTechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacityTechnologyRepository extends ReactiveCrudRepository<CapacityTechnologyEntity, Long> {
    Flux<CapacityTechnologyEntity> findByCapacityId(Long capacityId);

    Mono<Void> deleteByCapacityId(Long capacityId);
}

package com.pragma.ms_capacity.domain.spi;

import com.pragma.ms_capacity.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface ITechnologyClientPort {
    Mono<Technology> findById(Long id);

    Mono<Void> delete(Long id);
}

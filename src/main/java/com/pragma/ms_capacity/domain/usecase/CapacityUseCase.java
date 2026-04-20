package com.pragma.ms_capacity.domain.usecase;

import com.pragma.ms_capacity.domain.api.ICapacityServicePort;
import com.pragma.ms_capacity.domain.exception.CapacityAlreadyExistsException;
import com.pragma.ms_capacity.domain.exception.TechnologyNotFoundException;
import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.model.Technology;
import com.pragma.ms_capacity.domain.spi.ICapacityPersistencePort;
import com.pragma.ms_capacity.domain.spi.ITechnologyClientPort;
import com.pragma.ms_capacity.domain.validator.CapacityValidator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class CapacityUseCase implements ICapacityServicePort {

    private final ICapacityPersistencePort capacityPersistencePort;
    private final ITechnologyClientPort technologyClientPort;

    public CapacityUseCase(ICapacityPersistencePort capacityPersistencePort,
                           ITechnologyClientPort technologyClientPort) {
        this.capacityPersistencePort = capacityPersistencePort;
        this.technologyClientPort = technologyClientPort;
    }

    @Override
    public Mono<Capacity> save(Capacity capacity) {
        return CapacityValidator.validate(capacity)
                .flatMap(c -> capacityPersistencePort.existsByName(c.getName()))
                .flatMap(exists -> {
                    if (exists)
                        return Mono.error(new CapacityAlreadyExistsException(capacity.getName()));
                    return validateTechnologiesExist(capacity.getTechnologies());
                })
                .flatMap(technologies -> {
                    capacity.setTechnologies(technologies);
                    return capacityPersistencePort.save(capacity);
                });
    }

    private Mono<List<Technology>> validateTechnologiesExist(List<Technology> technologies) {
        return Flux.fromIterable(technologies)
                .flatMap(tech -> technologyClientPort.findById(tech.getId())
                        .switchIfEmpty(Mono.error(
                                new TechnologyNotFoundException(tech.getId())
                        ))
                )
                .collectList();
    }
}
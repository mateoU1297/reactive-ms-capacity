package com.pragma.ms_capacity.infrastructure.out.adapter;

import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.spi.ICapacityPersistencePort;
import com.pragma.ms_capacity.infrastructure.out.entity.CapacityTechnologyEntity;
import com.pragma.ms_capacity.infrastructure.out.mapper.ICapacityEntityMapper;
import com.pragma.ms_capacity.infrastructure.out.repository.CapacityRepository;
import com.pragma.ms_capacity.infrastructure.out.repository.CapacityTechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class CapacityPersistenceAdapter implements ICapacityPersistencePort {

    private final CapacityRepository capacityRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final ICapacityEntityMapper capacityEntityMapper;

    @Override
    public Mono<Capacity> save(Capacity capacity) {
        return capacityRepository.save(capacityEntityMapper.toEntity(capacity))
                .flatMap(saved -> {
                    List<CapacityTechnologyEntity> relations = capacity.getTechnologies()
                            .stream()
                            .map(tech -> new CapacityTechnologyEntity(saved.getId(), tech.getId()))
                            .toList();

                    return capacityTechnologyRepository.saveAll(relations)
                            .collectList()
                            .thenReturn(saved);
                })
                .map(saved -> {
                    capacity.setId(saved.getId());
                    return capacity;
                });
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return capacityRepository.existsByName(name);
    }
}
package com.pragma.ms_capacity.infrastructure.out.adapter;

import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.model.PagedResult;
import com.pragma.ms_capacity.domain.spi.ICapacityPersistencePort;
import com.pragma.ms_capacity.domain.spi.ITechnologyClientPort;
import com.pragma.ms_capacity.infrastructure.out.entity.CapacityEntity;
import com.pragma.ms_capacity.infrastructure.out.entity.CapacityTechnologyEntity;
import com.pragma.ms_capacity.infrastructure.out.mapper.ICapacityEntityMapper;
import com.pragma.ms_capacity.infrastructure.out.repository.CapacityRepository;
import com.pragma.ms_capacity.infrastructure.out.repository.CapacityTechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class CapacityPersistenceAdapter implements ICapacityPersistencePort {

    private final CapacityRepository capacityRepository;
    private final CapacityTechnologyRepository capacityTechnologyRepository;
    private final ICapacityEntityMapper capacityEntityMapper;
    private final ITechnologyClientPort technologyClientPort;
    private final DatabaseClient databaseClient;

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
    public Mono<Capacity> findById(Long id) {
        return capacityRepository.findById(id)
                .flatMap(entity ->
                        capacityTechnologyRepository.findByCapacityId(entity.getId())
                                .flatMap(rel -> technologyClientPort.findById(rel.getTechnologyId()))
                                .collectList()
                                .map(technologies -> {
                                    Capacity capacity = capacityEntityMapper.toDomain(entity);
                                    capacity.setTechnologies(technologies);
                                    return capacity;
                                })
                );
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return capacityRepository.existsByName(name);
    }

    @Override
    public Mono<PagedResult<Capacity>> findAll(int page, int size, String sortBy, boolean ascending) {
        String direction = ascending ? "ASC" : "DESC";
        int offset = page * size;

        return capacityRepository.count()
                .flatMap(total -> {
                    Flux<CapacityEntity> capacities;

                    if (sortBy.equals("name")) {
                        Sort sort = Sort.by(
                                ascending ? Sort.Direction.ASC : Sort.Direction.DESC, "name"
                        );
                        capacities = capacityRepository.findAllBy(PageRequest.of(page, size, sort));
                    } else {
                        String sql = """
                                SELECT c.id, c.name, c.description
                                FROM ms_capacity.capacity c
                                LEFT JOIN ms_capacity.capacity_technology ct
                                    ON c.id = ct.capacity_id
                                GROUP BY c.id, c.name, c.description
                                ORDER BY COUNT(ct.technology_id) %s
                                LIMIT %d OFFSET %d
                                """.formatted(direction, size, offset);

                        capacities = databaseClient.sql(sql)
                                .map((row, meta) -> new CapacityEntity(
                                        row.get("id", Long.class),
                                        row.get("name", String.class),
                                        row.get("description", String.class)
                                ))
                                .all();
                    }

                    return capacities
                            .flatMap(this::mapWithTechnologies)
                            .collectList()
                            .map(list -> new PagedResult<>(
                                    list,
                                    page,
                                    size,
                                    total,
                                    (int) Math.ceil((double) total / size)
                            ));
                });
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        return capacityTechnologyRepository.findByCapacityId(id)
                .flatMap(rel ->
                        capacityTechnologyRepository.countByTechnologyId(rel.getTechnologyId())
                                .flatMap(count -> {
                                    if (count <= 1)
                                        return technologyClientPort.delete(rel.getTechnologyId());

                                    return Mono.empty();
                                })
                )
                .then(capacityTechnologyRepository.deleteByCapacityId(id))
                .then(capacityRepository.deleteById(id));
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return capacityRepository.existsById(id);
    }

    private Mono<Capacity> mapWithTechnologies(CapacityEntity entity) {
        return capacityTechnologyRepository.findByCapacityId(entity.getId())
                .flatMap(rel -> technologyClientPort.findById(rel.getTechnologyId()))
                .collectList()
                .map(technologies -> {
                    Capacity capacity = capacityEntityMapper.toDomain(entity);
                    capacity.setTechnologies(technologies);
                    return capacity;
                });
    }
}
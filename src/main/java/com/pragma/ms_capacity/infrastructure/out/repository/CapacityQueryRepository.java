package com.pragma.ms_capacity.infrastructure.out.repository;

import com.pragma.ms_capacity.infrastructure.out.entity.CapacityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class CapacityQueryRepository {

    private final DatabaseClient databaseClient;

    private static final String FIND_ALL_ORDER_BY_NAME = """
            SELECT id, name, description
            FROM ms_capacity.capacity
            ORDER BY name %s
            LIMIT %d OFFSET %d
            """;

    private static final String FIND_ALL_ORDER_BY_TECHNOLOGY_COUNT = """
            SELECT c.id, c.name, c.description
            FROM ms_capacity.capacity c
            LEFT JOIN ms_capacity.capacity_technology ct
                ON c.id = ct.capacity_id
            GROUP BY c.id, c.name, c.description
            ORDER BY COUNT(ct.technology_id) %s
            LIMIT %d OFFSET %d
            """;

    public Flux<CapacityEntity> findAllOrderByName(String direction, int size, int offset) {
        return databaseClient.sql(FIND_ALL_ORDER_BY_NAME.formatted(direction, size, offset))
                .map((row, meta) -> new CapacityEntity(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("description", String.class)
                ))
                .all();
    }

    public Flux<CapacityEntity> findAllOrderByTechnologyCount(String direction,
                                                              int size, int offset) {
        return databaseClient.sql(FIND_ALL_ORDER_BY_TECHNOLOGY_COUNT.formatted(direction, size, offset))
                .map((row, meta) -> new CapacityEntity(
                        row.get("id", Long.class),
                        row.get("name", String.class),
                        row.get("description", String.class)
                ))
                .all();
    }
}

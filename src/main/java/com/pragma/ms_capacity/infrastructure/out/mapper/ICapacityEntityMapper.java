package com.pragma.ms_capacity.infrastructure.out.mapper;

import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.infrastructure.out.entity.CapacityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ICapacityEntityMapper {
    CapacityEntity toEntity(Capacity capacity);

    Capacity toDomain(CapacityEntity entity);
}

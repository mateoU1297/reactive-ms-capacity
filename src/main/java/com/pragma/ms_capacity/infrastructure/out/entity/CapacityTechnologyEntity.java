package com.pragma.ms_capacity.infrastructure.out.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacity_technology")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacityTechnologyEntity {

    private Long capacityId;

    private Long technologyId;
}

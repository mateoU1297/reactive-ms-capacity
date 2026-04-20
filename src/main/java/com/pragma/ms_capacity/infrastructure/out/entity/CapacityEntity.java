package com.pragma.ms_capacity.infrastructure.out.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacityEntity {

    @Id
    private Long id;

    private String name;

    private String description;
}

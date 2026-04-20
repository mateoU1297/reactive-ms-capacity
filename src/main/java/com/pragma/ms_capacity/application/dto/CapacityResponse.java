package com.pragma.ms_capacity.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacityResponse {
    private Long id;
    private String name;
    private String description;
    private List<TechnologyResponse> technologies;
}

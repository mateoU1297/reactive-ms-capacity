package com.pragma.ms_capacity.domain.exception;

public class CapacityAlreadyExistsException extends RuntimeException {
    public CapacityAlreadyExistsException(String name) {
        super("Capacity with name '" + name + "' already exists");
    }
}

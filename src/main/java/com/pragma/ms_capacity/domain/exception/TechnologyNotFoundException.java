package com.pragma.ms_capacity.domain.exception;

public class TechnologyNotFoundException extends RuntimeException {
    public TechnologyNotFoundException(Long id) {
        super("Technology with id " + id + " not found");
    }
}

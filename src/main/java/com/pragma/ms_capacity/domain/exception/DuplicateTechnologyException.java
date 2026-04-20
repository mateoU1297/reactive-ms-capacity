package com.pragma.ms_capacity.domain.exception;

public class DuplicateTechnologyException extends RuntimeException {
    public DuplicateTechnologyException() {
        super("Capacity cannot have duplicate technologies");
    }
}

package com.pragma.ms_capacity.domain.validator;

import com.pragma.ms_capacity.domain.exception.DuplicateTechnologyException;
import com.pragma.ms_capacity.domain.exception.InvalidFieldException;
import com.pragma.ms_capacity.domain.exception.InvalidTechnologyCountException;
import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.model.Technology;
import reactor.core.publisher.Mono;

import java.util.List;

public class CapacityValidator {

    private static final int MIN_TECHNOLOGIES = 3;
    private static final int MAX_TECHNOLOGIES = 20;
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 90;

    private CapacityValidator() {}

    public static Mono<Capacity> validate(Capacity capacity) {
        return validateName(capacity.getName())
                .then(validateDescription(capacity.getDescription()))
                .then(validateTechnologies(capacity.getTechnologies()))
                .thenReturn(capacity);
    }

    private static Mono<Void> validateName(String name) {
        if (name == null || name.isBlank())
            return Mono.error(new InvalidFieldException("Name is required"));
        if (name.length() > MAX_NAME_LENGTH)
            return Mono.error(new InvalidFieldException(
                    "Name must not exceed " + MAX_NAME_LENGTH + " characters"));
        return Mono.empty();
    }

    private static Mono<Void> validateDescription(String description) {
        if (description == null || description.isBlank())
            return Mono.error(new InvalidFieldException("Description is required"));
        if (description.length() > MAX_DESCRIPTION_LENGTH)
            return Mono.error(new InvalidFieldException(
                    "Description must not exceed " + MAX_DESCRIPTION_LENGTH + " characters"));
        return Mono.empty();
    }

    private static Mono<Void> validateTechnologies(List<Technology> technologies) {
        if (technologies == null || technologies.size() < MIN_TECHNOLOGIES)
            return Mono.error(new InvalidTechnologyCountException(
                    "Capacity must have at least " + MIN_TECHNOLOGIES + " technologies"));

        if (technologies.size() > MAX_TECHNOLOGIES)
            return Mono.error(new InvalidTechnologyCountException(
                    "Capacity must have at most " + MAX_TECHNOLOGIES + " technologies"));

        long distinctCount = technologies.stream()
                .map(Technology::getId)
                .distinct()
                .count();

        if (distinctCount < technologies.size())
            return Mono.error(new DuplicateTechnologyException());

        return Mono.empty();
    }
}

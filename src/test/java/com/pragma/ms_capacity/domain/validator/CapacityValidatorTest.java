package com.pragma.ms_capacity.domain.validator;

import com.pragma.ms_capacity.domain.exception.DuplicateTechnologyException;
import com.pragma.ms_capacity.domain.exception.InvalidFieldException;
import com.pragma.ms_capacity.domain.exception.InvalidTechnologyCountException;
import com.pragma.ms_capacity.domain.model.Capacity;
import com.pragma.ms_capacity.domain.model.Technology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

class CapacityValidatorTest {

    private List<Technology> validTechs;

    @BeforeEach
    void setUp() {
        validTechs = List.of(
                new Technology(1L, "Java"),
                new Technology(2L, "Spring"),
                new Technology(3L, "Docker")
        );
    }

    @Test
    void validate_validCapacity_success() {
        Capacity capacity = new Capacity(null, "Backend", "Description", validTechs);
        StepVerifier.create(CapacityValidator.validate(capacity))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void validate_nullName_throwsInvalidField() {
        Capacity capacity = new Capacity(null, null, "Description", validTechs);
        StepVerifier.create(CapacityValidator.validate(capacity))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_nameTooLong_throwsInvalidField() {
        Capacity capacity = new Capacity(null, "A".repeat(51), "Description", validTechs);
        StepVerifier.create(CapacityValidator.validate(capacity))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_nullDescription_throwsInvalidField() {
        Capacity capacity = new Capacity(null, "Backend", null, validTechs);
        StepVerifier.create(CapacityValidator.validate(capacity))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_descriptionTooLong_throwsInvalidField() {
        Capacity capacity = new Capacity(null, "Backend", "A".repeat(91), validTechs);
        StepVerifier.create(CapacityValidator.validate(capacity))
                .expectError(InvalidFieldException.class)
                .verify();
    }

    @Test
    void validate_lessThanMinTechs_throwsInvalidCount() {
        Capacity capacity = new Capacity(null, "Backend", "Description",
                List.of(new Technology(1L, "Java"), new Technology(2L, "Spring")));
        StepVerifier.create(CapacityValidator.validate(capacity))
                .expectError(InvalidTechnologyCountException.class)
                .verify();
    }

    @Test
    void validate_moreThanMaxTechs_throwsInvalidCount() {
        List<Technology> tooMany = new ArrayList<>();
        for (long i = 1; i <= 21; i++)
            tooMany.add(new Technology(i, "Tech" + i));
        Capacity capacity = new Capacity(null, "Backend", "Description", tooMany);
        StepVerifier.create(CapacityValidator.validate(capacity))
                .expectError(InvalidTechnologyCountException.class)
                .verify();
    }

    @Test
    void validate_duplicateTechs_throwsDuplicate() {
        Capacity capacity = new Capacity(null, "Backend", "Description",
                List.of(
                        new Technology(1L, "Java"),
                        new Technology(1L, "Java"),
                        new Technology(2L, "Spring")
                ));
        StepVerifier.create(CapacityValidator.validate(capacity))
                .expectError(DuplicateTechnologyException.class)
                .verify();
    }

    @Test
    void validatePagination_success() {
        StepVerifier.create(CapacityValidator.validatePagination(0, 10, "name"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePagination_sortByTechnologyCount() {
        StepVerifier.create(CapacityValidator.validatePagination(0, 10, "technologyCount"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePagination_throwsInvalidField() {
        StepVerifier.create(CapacityValidator.validatePagination(-1, 10, "name"))
                .expectErrorMatches(e -> e instanceof InvalidFieldException &&
                        e.getMessage().equals("Page must be >= 0"))
                .verify();
    }

    @Test
    void validatePagination_zeroSize() {
        StepVerifier.create(CapacityValidator.validatePagination(0, 0, "name"))
                .expectErrorMatches(e -> e instanceof InvalidFieldException &&
                        e.getMessage().equals("Size must be > 0"))
                .verify();
    }

    @Test
    void validatePagination_negativeSize() {
        StepVerifier.create(CapacityValidator.validatePagination(0, -1, "name"))
                .expectErrorMatches(e -> e instanceof InvalidFieldException &&
                        e.getMessage().equals("Size must be > 0"))
                .verify();
    }

    @Test
    void validatePagination_invalidSort() {
        StepVerifier.create(CapacityValidator.validatePagination(0, 10, "invalid"))
                .expectErrorMatches(e -> e instanceof InvalidFieldException &&
                        e.getMessage().contains("SortBy must be one of"))
                .verify();
    }
}

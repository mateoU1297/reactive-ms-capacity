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
}
